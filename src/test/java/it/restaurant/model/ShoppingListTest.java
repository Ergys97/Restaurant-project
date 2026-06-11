package it.restaurant.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ShoppingListTest {

    private static final LocalDate DAY = LocalDate.of(2026, 7, 1);

    @Test
    void mergeSumsQuantitiesOfSameNameCaseInsensitive() {
        ShoppingList target = new ShoppingList(
                new ArrayList<>(List.of(new Ingredient("Farina", 5, DAY))), new ArrayList<>(), new ArrayList<>());
        ShoppingList other = new ShoppingList(
                new ArrayList<>(List.of(new Ingredient("farina", 3, DAY), new Ingredient("uova", 6, DAY))),
                new ArrayList<>(), new ArrayList<>());

        target.merge(other);

        assertEquals(2, target.getIngredients().size());
        assertEquals(8, FoodItems.findByName(target.getIngredients(), "farina").orElseThrow().getQuantity());
    }

    @Test
    void isEmptyOnlyWhenAllListsEmpty() {
        assertTrue(new ShoppingList().isEmpty());
        assertFalse(new ShoppingList(
                new ArrayList<>(List.of(new Ingredient("sale", 1, DAY))),
                new ArrayList<>(), new ArrayList<>()).isEmpty());
    }
}
