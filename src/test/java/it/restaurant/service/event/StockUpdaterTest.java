package it.restaurant.service.event;

import static org.junit.jupiter.api.Assertions.*;

import it.restaurant.model.*;
import it.restaurant.repository.*;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class StockUpdaterTest {

    private static final LocalDate DAY = LocalDate.of(2026, 7, 1);
    @TempDir Path tempDir;

    @Test
    void confirmedReservationDecreasesStock() {
        JsonDataStore store = new JsonDataStore(tempDir);
        store.saveList(StorageKeys.INGREDIENTS, List.of(new Ingredient("salame", 15, DAY.plusDays(5))));
        store.saveList(StorageKeys.DRINKS, List.of(new Drink("acqua", 30, DAY.plusDays(5))));
        RestaurantConfig config = new RestaurantConfig(20, 5.0,
                List.of(), List.of(), List.of(), Map.of("acqua", 2), Map.of());

        Recipe recipe = new Recipe("salame", List.of(new Ingredient("salame", 1, DAY)), 0.4, 5.0, 10);
        Reservation reservation = new Reservation(DAY, 3);
        reservation.addDishOrder(new Dish(recipe, DAY.plusDays(10)), 4);

        new StockUpdater(store, config).onReservationConfirmed(reservation);

        assertEquals(11, store.loadList(StorageKeys.INGREDIENTS, Ingredient.class).get(0).getQuantity()); // 15-4
        assertEquals(24, store.loadList(StorageKeys.DRINKS, Drink.class).get(0).getQuantity()); // 30-2*3
    }

    @Test
    void stockDoesNotGoNegative() {
        JsonDataStore store = new JsonDataStore(tempDir);
        store.saveList(StorageKeys.INGREDIENTS, List.of(new Ingredient("salame", 2, DAY.plusDays(5))));
        store.saveList(StorageKeys.DRINKS, List.of(new Drink("acqua", 3, DAY.plusDays(5))));
        RestaurantConfig config = new RestaurantConfig(20, 5.0,
                List.of(), List.of(), List.of(), Map.of("acqua", 2), Map.of());

        Recipe recipe = new Recipe("salame", List.of(new Ingredient("salame", 1, DAY)), 0.4, 5.0, 10);
        Reservation reservation = new Reservation(DAY, 3);
        reservation.addDishOrder(new Dish(recipe, DAY.plusDays(10)), 4);

        new StockUpdater(store, config).onReservationConfirmed(reservation);

        assertEquals(0, store.loadList(StorageKeys.INGREDIENTS, Ingredient.class).get(0).getQuantity());
        assertEquals(0, store.loadList(StorageKeys.DRINKS, Drink.class).get(0).getQuantity());
    }
}
