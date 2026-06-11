package it.restaurant.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class ReservationTest {

    private static final LocalDate DAY = LocalDate.of(2026, 7, 1);

    private Dish dish(String name, double fraction) {
        Recipe recipe = new Recipe(name, List.of(new Ingredient(name, 1, DAY)), fraction, 1.0, 10);
        return new Dish(recipe, DAY.plusDays(10));
    }

    @Test
    void addDishOrderMergesSameDish() {
        Reservation r = new Reservation(DAY, 4);
        Dish d = dish("salame", 0.4);
        r.addDishOrder(d, 2);
        r.addDishOrder(d, 1);
        assertEquals(1, r.getDishOrders().size());
        assertEquals(3, r.getDishOrders().get(0).getQuantity());
    }

    @Test
    void workloadSumsDishAndMenuOrders() {
        Reservation r = new Reservation(DAY, 4);
        r.addDishOrder(dish("a", 0.4), 2);                       // 0.8
        r.addMenuOrder(new ThemedMenu("menu", List.of(dish("b", 0.5)), DAY.plusDays(5)), 1); // 0.5
        assertEquals(1.3, r.workload(), 1e-9);
    }
}
