package it.restaurant.controller;

import it.restaurant.model.ShoppingList;
import it.restaurant.service.WarehouseService;
import it.restaurant.service.event.ReservationNotifier;
import it.restaurant.view.*;
import java.time.LocalDate;

public class WarehouseController {

    private static final String[] OPTIONS = {"Visualizza elementi nel magazzino", "Elabora lista della spesa"};

    private final WarehouseService warehouseService;
    private final ReservationNotifier notifier;
    private final ConsoleView view;
    private final Menu menu = new Menu("Menu magazziniere", OPTIONS);

    public WarehouseController(WarehouseService warehouseService, ReservationNotifier notifier, ConsoleView view) {
        this.warehouseService = warehouseService;
        this.notifier = notifier;
        this.view = view;
    }

    public void run() {
        int choice;
        do {
            checkNotifications();
            choice = menu.choose();
            switch (choice) {
                case 1 -> view.showShoppingList(warehouseService.currentStock());
                case 2 -> warehouseService.restockAll(LocalDate.now());
                default -> { }
            }
        } while (choice != 0);
    }

    private void checkNotifications() {
        if (notifier.consumePending()) {
            ShoppingList received = warehouseService.receivePendingShoppingList();
            view.showLine(Messages.FOOD_LIST_FROM_RESERVATIONS);
            view.showShoppingList(received);
        }
    }
}
