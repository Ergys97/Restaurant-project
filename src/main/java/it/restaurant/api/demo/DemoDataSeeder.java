package it.restaurant.api.demo;

import it.restaurant.model.Dish;
import it.restaurant.model.Drink;
import it.restaurant.model.ExtraGood;
import it.restaurant.model.Ingredient;
import it.restaurant.model.Recipe;
import it.restaurant.model.Reservation;
import it.restaurant.model.RestaurantConfig;
import it.restaurant.model.ShoppingList;
import it.restaurant.model.ThemedMenu;
import it.restaurant.repository.DataStore;
import it.restaurant.repository.StorageKeys;
import it.restaurant.util.ExpiryDates;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DemoDataSeeder {

    private final DataStore store;

    public DemoDataSeeder(DataStore store) {
        this.store = store;
    }

    public DemoResetSummary seed() {
        LocalDate today = LocalDate.now();

        Ingredient insalata = new Ingredient("insalata", 10, ExpiryDates.random(today));
        Ingredient pomodoro = new Ingredient("pomodoro", 15, ExpiryDates.random(today));
        Ingredient farina = new Ingredient("farina", 20, ExpiryDates.random(today));
        Ingredient mozzarella = new Ingredient("mozzarella", 12, ExpiryDates.random(today));
        Ingredient basilico = new Ingredient("basilico", 8, ExpiryDates.random(today));
        List<Ingredient> stockIngredients = List.of(insalata, pomodoro, farina, mozzarella, basilico);
        store.saveList(StorageKeys.INGREDIENTS, stockIngredients);

        Drink acqua = new Drink("acqua", 30, ExpiryDates.inDays(today, 180));
        Drink vino = new Drink("vino", 20, ExpiryDates.inDays(today, 365));
        store.saveList(StorageKeys.DRINKS, List.of(acqua, vino));

        ExtraGood olio = new ExtraGood("olio d'oliva", 5, ExpiryDates.inDays(today, 120));
        store.saveList(StorageKeys.EXTRA_GOODS, List.of(olio));

        Recipe caprese = new Recipe("Insalata Caprese",
                List.of(mozzarella, pomodoro, basilico), 0.3, 2.0, 10);
        Recipe pizza = new Recipe("Pizza Margherita",
                List.of(farina, pomodoro, mozzarella, basilico), 0.5, 2.0, 15);
        store.saveList(StorageKeys.RECIPES, List.of(caprese, pizza));

        Dish capreseDish = new Dish(caprese, today.plusDays(7));
        Dish pizzaDish = new Dish(pizza, today.plusDays(7));
        store.saveList(StorageKeys.DISHES, List.of(capreseDish, pizzaDish));

        ThemedMenu menu = new ThemedMenu("Menu Italiano",
                List.of(capreseDish, pizzaDish), today.plusDays(7));
        store.saveList(StorageKeys.THEMED_MENUS, List.of(menu));

        Reservation reservation = new Reservation(today.plusDays(1), 4);
        reservation.addDishOrder(pizzaDish, 2);
        reservation.addMenuOrder(menu, 1);
        store.saveList(StorageKeys.RESERVATIONS, List.of(reservation));

        ShoppingList shoppingList = new ShoppingList(
                List.of(new Ingredient("farina", 10, ExpiryDates.random(today))),
                List.of(new Drink("acqua", 12, ExpiryDates.inDays(today, 180))),
                List.of());
        store.save(StorageKeys.SHOPPING_LIST, shoppingList);

        RestaurantConfig config = new RestaurantConfig(25, 2.0,
                stockIngredients, List.of(acqua, vino), List.of(olio),
                Map.of("acqua", 1, "vino", 1),
                Map.of("olio d'oliva", 1));
        store.save(StorageKeys.RESTAURANT_CONFIG, config);

        return new DemoResetSummary(1);
    }
}
