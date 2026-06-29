package it.restaurant.service.event;

import static org.junit.jupiter.api.Assertions.*;

import it.restaurant.model.*;
import it.restaurant.repository.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ShoppingListUpdaterTest {

    @Test
    void shoppingListUsesRecipeIngredientQuantityTimesOrderedPortions() {
        LocalDate day = LocalDate.of(2026, 7, 1);
        InMemoryStore store = new InMemoryStore();
        RestaurantConfig config = new RestaurantConfig(20, 2.0,
                List.of(), List.of(), List.of(), Map.of(), Map.of());
        Recipe recipe = new Recipe("Pizza", List.of(new Ingredient("farina", 3, day)), 0.5, 2.0, 10);
        Dish dish = new Dish(recipe, day.plusDays(3));
        Reservation reservation = new Reservation(day, 2);
        reservation.addDishOrder(dish, 4);

        new ShoppingListUpdater(store, config).onReservationConfirmed(reservation);

        ShoppingList shoppingList = store.load(StorageKeys.SHOPPING_LIST, ShoppingList.class).orElseThrow();
        assertEquals(12, shoppingList.getIngredients().get(0).getQuantity());
    }
}
