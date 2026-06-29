package it.restaurant.service;

import it.restaurant.model.Reservation;
import it.restaurant.model.RestaurantConfig;
import it.restaurant.repository.DataStore;
import it.restaurant.repository.DataStoreTransaction;
import it.restaurant.repository.StorageKeys;
import it.restaurant.service.event.ReservationObserver;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservationService {

    private final RestaurantConfig config;
    private final DataStore store;
    private final DataStoreTransaction transaction;
    private final List<ReservationObserver> observers = new ArrayList<>();

    public ReservationService(RestaurantConfig config, DataStore store, DataStoreTransaction transaction) {
        this.config = config;
        this.store = store;
        this.transaction = transaction;
    }

    public void addObserver(ReservationObserver observer) { observers.add(observer); }

    public int availableSeats(LocalDate date, List<Reservation> existing) {
        int taken = existing.stream()
                .filter(r -> r.getDate().equals(date))
                .mapToInt(Reservation::getCovers)
                .sum();
        return config.getSeats() - taken;
    }

    public double dayWorkload(LocalDate date, List<Reservation> reservations) {
        return reservations.stream()
                .filter(r -> r.getDate().equals(date))
                .mapToDouble(Reservation::workload)
                .sum();
    }

    public boolean isSustainable(Reservation candidate, List<Reservation> existing) {
        double total = dayWorkload(candidate.getDate(), existing) + candidate.workload();
        return total < config.getSustainableWorkload();
    }

    public List<Reservation> listReservations() {
        return store.loadList(StorageKeys.RESERVATIONS, Reservation.class);
    }

    private boolean checkStockSufficiency(Reservation candidate) {
        var ingredients = store.loadList(StorageKeys.INGREDIENTS, it.restaurant.model.Ingredient.class);
        var drinks = store.loadList(StorageKeys.DRINKS, it.restaurant.model.Drink.class);
        var extraGoods = store.loadList(StorageKeys.EXTRA_GOODS, it.restaurant.model.ExtraGood.class);

        // Map to aggregate required ingredients
        java.util.Map<String, Integer> requiredIngredients = new java.util.HashMap<>();
        for (var order : candidate.getDishOrders()) {
            for (var needed : order.getDish().getIngredients()) {
                requiredIngredients.merge(needed.getName().toLowerCase(),
                        needed.getQuantity() * order.getQuantity(),
                        Integer::sum);
            }
        }
        for (var order : candidate.getMenuOrders()) {
            for (var dish : order.getMenu().getDishes()) {
                for (var needed : dish.getIngredients()) {
                    requiredIngredients.merge(needed.getName().toLowerCase(),
                            needed.getQuantity() * order.getQuantity(),
                            Integer::sum);
                }
            }
        }

        // Validate ingredients
        for (var entry : requiredIngredients.entrySet()) {
            var matching = ingredients.stream()
                    .filter(i -> i.getName().equalsIgnoreCase(entry.getKey()))
                    .mapToInt(it.restaurant.model.Ingredient::getQuantity)
                    .sum();
            if (matching < entry.getValue()) {
                return false;
            }
        }

        // Validate drinks
        for (var entry : config.getPerCapitaDrinks().entrySet()) {
            int needed = entry.getValue() * candidate.getCovers();
            var matching = drinks.stream()
                    .filter(d -> d.getName().equalsIgnoreCase(entry.getKey()))
                    .mapToInt(it.restaurant.model.Drink::getQuantity)
                    .sum();
            if (matching < needed) {
                return false;
            }
        }

        // Validate extra goods
        for (var entry : config.getPerCapitaExtraGoods().entrySet()) {
            int needed = entry.getValue() * candidate.getCovers();
            var matching = extraGoods.stream()
                    .filter(eg -> eg.getName().equalsIgnoreCase(entry.getKey()))
                    .mapToInt(it.restaurant.model.ExtraGood::getQuantity)
                    .sum();
            if (matching < needed) {
                return false;
            }
        }

        return true;
    }

    public Optional<Reservation> createReservation(Reservation candidate) {
        return transaction.write(() -> {
            List<Reservation> existing = listReservations();
            return createReservation(candidate, existing);
        });
    }

    public Optional<Reservation> createReservation(Reservation candidate, List<Reservation> existing) {
        if (candidate == null
                || candidate.getCovers() > availableSeats(candidate.getDate(), existing)
                || !isSustainable(candidate, existing)
                || !checkStockSufficiency(candidate)) {
            return Optional.empty();
        }
        observers.forEach(o -> o.onReservationConfirmed(candidate));
        return Optional.of(candidate);
    }

    public boolean cancelReservation(String id) {
        return transaction.write(() -> {
            List<Reservation> all = store.loadList(StorageKeys.RESERVATIONS, Reservation.class);
            boolean removed = all.removeIf(r -> r.getId().equals(id));
            if (removed) {
                store.saveList(StorageKeys.RESERVATIONS, all);
            }
            return removed;
        });
    }

    public void cleanExpired() {
        transaction.write(() -> {
            List<Reservation> all = store.loadList(StorageKeys.RESERVATIONS, Reservation.class);
            boolean removed = all.removeIf(r -> r.getDate().isBefore(LocalDate.now()));
            if (removed) {
                store.saveList(StorageKeys.RESERVATIONS, all);
            }
        });
    }
}
