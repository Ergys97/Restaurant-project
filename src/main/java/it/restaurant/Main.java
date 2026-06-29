package it.restaurant;

import it.restaurant.controller.*;
import it.restaurant.model.RestaurantConfig;
import it.restaurant.repository.*;
import it.restaurant.service.*;
import it.restaurant.service.event.*;
import it.restaurant.view.*;
import java.nio.file.Path;

public class Main {

    private static final String INTRO = "---Ristorante di Ergys & Enrico---";
    private static final String[] ROLES = {"Gestore", "Addetto delle prenotazioni", "Magazziniere"};

    public static void main(String[] args) {
        System.out.println(INTRO);
        DataStore store = new JsonDataStore(Path.of("data"));
        ConsoleView view = new ConsoleView();

        ManagerController manager = new ManagerController(store, view);
        RestaurantConfig config = manager.ensureConfig();

        DataStoreTransaction transaction = new DataStoreTransaction();
        ReservationService reservationService = new ReservationService(config, store, transaction);
        KitchenService kitchenService = new KitchenService(store, config, transaction);
        WarehouseService warehouseService = new WarehouseService(store, transaction);
        ReservationNotifier notifier = new ReservationNotifier();
        reservationService.addObserver(new StockUpdater(store, config));
        reservationService.addObserver(new ShoppingListUpdater(store, config));
        reservationService.addObserver(new ReservationRegistry(store, notifier));

        ReservationController reservations =
                new ReservationController(store, reservationService, kitchenService, view);
        WarehouseController warehouse = new WarehouseController(warehouseService, notifier, view);

        Menu roleMenu = new Menu("Benvenuto nella gestione del ristorante! \nScegliere l'utente: ", ROLES);
        int choice;
        do {
            choice = roleMenu.choose();
            switch (choice) {
                case 1 -> manager.run();
                case 2 -> reservations.run();
                case 3 -> warehouse.run();
                default -> { }
            }
        } while (choice != 0);
        System.out.println("\nProgramma chiuso correttamente!");
    }
}
