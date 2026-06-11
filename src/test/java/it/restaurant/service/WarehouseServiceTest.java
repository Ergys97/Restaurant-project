package it.restaurant.service;

import static org.junit.jupiter.api.Assertions.*;

import it.restaurant.model.Ingredient;
import it.restaurant.repository.JsonDataStore;
import it.restaurant.repository.StorageKeys;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class WarehouseServiceTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 7, 1);
    @TempDir Path tempDir;

    @Test
    void depletedItemIsRestocked() {
        List<Ingredient> items = new ArrayList<>(List.of(new Ingredient("farina", 3, TODAY.plusDays(5))));
        new WarehouseService(new JsonDataStore(tempDir)).restock(items, TODAY);
        assertEquals(23, items.get(0).getQuantity());
    }

    @Test
    void negativeQuantityResetsToRestockAmount() {
        List<Ingredient> items = new ArrayList<>(List.of(new Ingredient("farina", -7, TODAY.plusDays(5))));
        new WarehouseService(new JsonDataStore(tempDir)).restock(items, TODAY);
        assertEquals(20, items.get(0).getQuantity());
    }

    @Test
    void expiredItemGetsNewFutureExpiryDate() {
        Ingredient expired = new Ingredient("latte", 10, TODAY.minusDays(1));
        new WarehouseService(new JsonDataStore(tempDir)).restock(new ArrayList<>(List.of(expired)), TODAY);
        assertFalse(expired.isExpired(TODAY));
        assertEquals(10, expired.getQuantity());
    }

    @Test
    void healthyItemIsUntouched() {
        Ingredient ok = new Ingredient("sale", 50, TODAY.plusDays(9));
        new WarehouseService(new JsonDataStore(tempDir)).restock(new ArrayList<>(List.of(ok)), TODAY);
        assertEquals(50, ok.getQuantity());
        assertEquals(TODAY.plusDays(9), ok.getExpiryDate());
    }
}
