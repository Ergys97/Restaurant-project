package it.restaurant.repository;

import static org.junit.jupiter.api.Assertions.*;

import it.restaurant.model.*;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JsonDataStoreTest {

    @TempDir Path tempDir;
    private JsonDataStore store;

    @BeforeEach
    void setUp() { store = new JsonDataStore(tempDir); }

    @Test
    void loadListOnMissingFileReturnsEmptyList() {
        assertTrue(store.loadList(StorageKeys.INGREDIENTS, Ingredient.class).isEmpty());
    }

    @Test
    void loadOnMissingFileReturnsEmptyOptional() {
        assertTrue(store.load(StorageKeys.RESTAURANT_CONFIG, RestaurantConfig.class).isEmpty());
    }

    @Test
    void listRoundTripPreservesData() {
        LocalDate expiry = LocalDate.of(2026, 7, 1);
        store.saveList(StorageKeys.INGREDIENTS,
                List.of(new Ingredient("farina", 10, expiry), new Ingredient("uova", 24, expiry)));

        List<Ingredient> loaded = store.loadList(StorageKeys.INGREDIENTS, Ingredient.class);

        assertEquals(2, loaded.size());
        assertEquals("farina", loaded.get(0).getName());
        assertEquals(10, loaded.get(0).getQuantity());
        assertEquals(expiry, loaded.get(0).getExpiryDate());
        assertEquals("KG", loaded.get(0).getUnit());
    }

    @Test
    void reservationRoundTripPreservesOrders() {
        LocalDate date = LocalDate.of(2026, 7, 1);
        Recipe recipe = new Recipe("salame", List.of(new Ingredient("salame", 15, date)), 0.4, 1.0, 10);
        Dish dish = new Dish(recipe, date.plusDays(10));
        Reservation reservation = new Reservation(date, 2);
        reservation.addDishOrder(dish, 2);
        store.saveList(StorageKeys.RESERVATIONS, List.of(reservation));

        List<Reservation> loaded = store.loadList(StorageKeys.RESERVATIONS, Reservation.class);

        assertEquals(1, loaded.size());
        assertEquals(2, loaded.get(0).getCovers());
        assertEquals(1, loaded.get(0).getDishOrders().size());
        assertEquals(0.8, loaded.get(0).workload(), 1e-9);
    }

    @Test
    void singleObjectRoundTrip() {
        RestaurantConfig config = new RestaurantConfig(20, 5.0,
                List.of(), List.of(), List.of(), java.util.Map.of("acqua", 1), java.util.Map.of());
        store.save(StorageKeys.RESTAURANT_CONFIG, config);

        Optional<RestaurantConfig> loaded = store.load(StorageKeys.RESTAURANT_CONFIG, RestaurantConfig.class);

        assertTrue(loaded.isPresent());
        assertTrue(loaded.get().isInitialized());
        assertEquals(20, loaded.get().getSeats());
        assertEquals(120.0, loaded.get().getSustainableWorkload(), 1e-9);
    }
}
