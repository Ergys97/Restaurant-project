package it.restaurant.service;

import it.restaurant.model.*;
import it.restaurant.repository.DataStore;
import it.restaurant.repository.StorageKeys;
import it.restaurant.util.ExpiryDates;
import java.time.LocalDate;
import java.util.List;

public class WarehouseService {

    static final int DEPLETION_THRESHOLD = 5;
    static final int RESTOCK_AMOUNT = 20;

    private final DataStore store;

    public WarehouseService(DataStore store) { this.store = store; }

    public ShoppingList currentStock() {
        return new ShoppingList(
                store.loadList(StorageKeys.INGREDIENTS, Ingredient.class),
                store.loadList(StorageKeys.DRINKS, Drink.class),
                store.loadList(StorageKeys.EXTRA_GOODS, ExtraGood.class));
    }

    public void restockAll(LocalDate today) {
        ShoppingList stock = currentStock();
        restock(stock.getIngredients(), today);
        restock(stock.getDrinks(), today);
        restock(stock.getExtraGoods(), today);
        store.saveList(StorageKeys.INGREDIENTS, stock.getIngredients());
        store.saveList(StorageKeys.DRINKS, stock.getDrinks());
        store.saveList(StorageKeys.EXTRA_GOODS, stock.getExtraGoods());
    }

    <T extends FoodItem> void restock(List<T> items, LocalDate today) {
        for (T item : items) {
            if (item.isExpired(today)) {
                item.setExpiryDate(ExpiryDates.random(today));
            }
            if (item.getQuantity() <= DEPLETION_THRESHOLD) {
                int base = Math.max(item.getQuantity(), 0);
                item.setQuantity(base + RESTOCK_AMOUNT);
            }
        }
    }

    public ShoppingList receivePendingShoppingList() {
        ShoppingList pending = store.load(StorageKeys.SHOPPING_LIST, ShoppingList.class)
                .orElseGet(ShoppingList::new);
        if (pending.isEmpty()) {
            return pending;
        }
        ShoppingList stock = currentStock();
        stock.merge(pending);
        store.saveList(StorageKeys.INGREDIENTS, stock.getIngredients());
        store.saveList(StorageKeys.DRINKS, stock.getDrinks());
        store.saveList(StorageKeys.EXTRA_GOODS, stock.getExtraGoods());
        store.save(StorageKeys.SHOPPING_LIST, new ShoppingList());
        return pending;
    }
}
