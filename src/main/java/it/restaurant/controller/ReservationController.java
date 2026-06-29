package it.restaurant.controller;

import it.restaurant.model.*;
import it.restaurant.repository.DataStore;
import it.restaurant.repository.StorageKeys;
import it.restaurant.service.KitchenService;
import it.restaurant.service.ReservationService;
import it.restaurant.util.ExpiryDates;
import it.restaurant.view.*;
import java.time.LocalDate;
import java.util.List;

public class ReservationController {

    private static final String[] OPTIONS = {"Raccogli prenotazioni", "Visualizza prenotazioni"};

    private final DataStore store;
    private final ReservationService reservationService;
    private final KitchenService kitchenService;
    private final ConsoleView view;
    private final Menu menu = new Menu("Menu addetto delle prenotazioni", OPTIONS);

    public ReservationController(DataStore store, ReservationService reservationService,
                                 KitchenService kitchenService, ConsoleView view) {
        this.store = store;
        this.reservationService = reservationService;
        this.kitchenService = kitchenService;
        this.view = view;
    }

    public void run() {
        int choice;
        do {
            reservationService.cleanExpired();
            choice = menu.choose();
            switch (choice) {
                case 1 -> collectReservation();
                case 2 -> view.showReservations(store.loadList(StorageKeys.RESERVATIONS, Reservation.class));
                default -> { }
            }
        } while (choice != 0);
    }

    private void collectReservation() {
        LocalDate today = LocalDate.now();
        List<ThemedMenu> menus = kitchenService.themedMenus();
        List<Dish> dishes = kitchenService.availableDishes(today);
        if (menus.isEmpty() && dishes.isEmpty()) {
            view.showLine(Messages.NO_MENU_OR_DISHES);
            return;
        }
        List<Reservation> existing = reservationService.listReservations();
        int days = ConsoleInput.readIntAtLeast(Messages.ASK_DAYS_FOR_RESERVATION, 0);
        LocalDate date = ExpiryDates.inDays(today, days);
        int maxSeats = reservationService.availableSeats(date, existing);
        if (maxSeats <= 0) {
            view.showLine(Messages.RESTAURANT_FULL);
            return;
        }
        int covers = ConsoleInput.readInt(Messages.ASK_PEOPLE_FOR_RESERVATION, 1, maxSeats);
        Reservation reservation = new Reservation(date, covers);
        fillOrders(reservation, menus, dishes, covers);
        boolean accepted = reservationService.createReservation(reservation).isPresent();
        view.showLine(accepted ? Messages.RESERVATION_ACCEPTED : Messages.RESERVATION_REJECTED);
    }

    private void fillOrders(Reservation reservation, List<ThemedMenu> menus, List<Dish> dishes, int covers) {
        int remaining = covers;
        do {
            view.showLine(Messages.REMAINING_PEOPLE + remaining);
            if (ConsoleInput.readYesNo(Messages.SHOW_THEMATIC_MENUS_PROMPT) && !menus.isEmpty()) {
                ThemedMenu chosen = ConsoleInput.selectOne(menus, ThemedMenu::getName, Messages.SELECT_THEMATIC_MENU_PROMPT);
                int qty = ConsoleInput.readInt(Messages.PEOPLE_FOR_MENU_PROMPT, 1, remaining);
                reservation.addMenuOrder(chosen, qty);
                remaining -= qty;
            } else if (!dishes.isEmpty()) {
                Dish chosen = ConsoleInput.selectOne(dishes, Dish::getName, Messages.SELECT_DISH_PROMPT);
                int qty = ConsoleInput.readInt(Messages.PEOPLE_FOR_DISH_PROMPT, 1, remaining);
                reservation.addDishOrder(chosen, qty);
                remaining -= qty;
            }
        } while (remaining > 0);
    }
}
