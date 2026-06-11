package it.restaurant.service.event;

import it.restaurant.model.*;
import it.restaurant.repository.DataStore;
import it.restaurant.repository.StorageKeys;
import it.restaurant.util.ExpiryDates;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ShoppingListUpdater implements ReservationObserver {

    private final DataStore store;
    private final RestaurantConfig config;

    public ShoppingListUpdater(DataStore store, RestaurantConfig config) {
        this.store = store;
        this.config = config;
    }

    @Override
    public void onReservationConfirmed(Reservation reservation) {
        LocalDate today = LocalDate.now();
        ShoppingList fromReservation = new ShoppingList();

        for (DishOrder order : reservation.getDishOrders()) {
            addDishIngredients(fromReservation.getIngredients(), order.getDish(), order.getQuantity(), today);
        }
        for (MenuOrder order : reservation.getMenuOrders()) {
            for (Dish dish : order.getMenu().getDishes()) {
                addDishIngredients(fromReservation.getIngredients(), dish, order.getQuantity(), today);
            }
        }
        config.getPerCapitaDrinks().forEach((name, perCapita) ->
                FoodItems.mergeQuantity(fromReservation.getDrinks(),
                        new Drink(name, perCapita * reservation.getCovers(), ExpiryDates.random(today))));
        config.getPerCapitaExtraGoods().forEach((name, perCapita) ->
                FoodItems.mergeQuantity(fromReservation.getExtraGoods(),
                        new ExtraGood(name, perCapita * reservation.getCovers(), ExpiryDates.random(today))));

        ShoppingList saved = store.load(StorageKeys.SHOPPING_LIST, ShoppingList.class)
                .orElseGet(ShoppingList::new);
        saved.merge(fromReservation);
        store.save(StorageKeys.SHOPPING_LIST, saved);
    }

    private void addDishIngredients(List<Ingredient> target, Dish dish, int portions, LocalDate today) {
        for (Ingredient needed : dish.getIngredients()) {
            FoodItems.mergeQuantity(target,
                    new Ingredient(needed.getName(), portions, ExpiryDates.random(today)));
        }
    }
}
