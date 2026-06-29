package it.restaurant.service.event;

import it.restaurant.model.*;
import it.restaurant.repository.DataStore;
import it.restaurant.repository.StorageKeys;

public class StockUpdater implements ReservationObserver {

    private final DataStore store;
    private final RestaurantConfig config;

    public StockUpdater(DataStore store, RestaurantConfig config) {
        this.store = store;
        this.config = config;
    }

    @Override
    public void onReservationConfirmed(Reservation reservation) {
        var ingredients = store.loadList(StorageKeys.INGREDIENTS, Ingredient.class);
        var drinks = store.loadList(StorageKeys.DRINKS, Drink.class);
        var extraGoods = store.loadList(StorageKeys.EXTRA_GOODS, ExtraGood.class);

        for (DishOrder order : reservation.getDishOrders()) {
            consumeDishIngredients(ingredients, order.getDish(), order.getQuantity());
        }
        for (MenuOrder order : reservation.getMenuOrders()) {
            for (Dish dish : order.getMenu().getDishes()) {
                consumeDishIngredients(ingredients, dish, order.getQuantity());
            }
        }
        config.getPerCapitaDrinks().forEach((name, perCapita) ->
                FoodItems.findByName(drinks, name).ifPresent(d ->
                        d.setQuantity(Math.max(0, d.getQuantity() - perCapita * reservation.getCovers()))));
        config.getPerCapitaExtraGoods().forEach((name, perCapita) ->
                FoodItems.findByName(extraGoods, name).ifPresent(e ->
                        e.setQuantity(Math.max(0, e.getQuantity() - perCapita * reservation.getCovers()))));

        store.saveList(StorageKeys.INGREDIENTS, ingredients);
        store.saveList(StorageKeys.DRINKS, drinks);
        store.saveList(StorageKeys.EXTRA_GOODS, extraGoods);
     }
 
     private void consumeDishIngredients(java.util.List<Ingredient> stock, Dish dish, int portions) {
         for (Ingredient needed : dish.getIngredients()) {
             int required = needed.getQuantity() * portions;
             FoodItems.findByName(stock, needed.getName())
                     .ifPresent(i -> i.setQuantity(Math.max(0, i.getQuantity() - required)));
         }
     }
}
