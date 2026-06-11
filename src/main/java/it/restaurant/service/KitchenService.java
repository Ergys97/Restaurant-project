package it.restaurant.service;

import it.restaurant.model.*;
import it.restaurant.repository.DataStore;
import it.restaurant.repository.StorageKeys;
import java.time.LocalDate;
import java.util.List;

public class KitchenService {

    private static final double MENU_ACCEPTABILITY_FACTOR = 4.0 / 3.0;

    private final DataStore store;
    private RestaurantConfig config;

    public KitchenService(DataStore store, RestaurantConfig config) {
        this.store = store;
        this.config = config;
    }

    public void setConfig(RestaurantConfig config) { this.config = config; }

    public List<Recipe> recipes() { return store.loadList(StorageKeys.RECIPES, Recipe.class); }
    public List<Dish> dishes() { return store.loadList(StorageKeys.DISHES, Dish.class); }
    public List<ThemedMenu> themedMenus() { return store.loadList(StorageKeys.THEMED_MENUS, ThemedMenu.class); }

    public List<Dish> availableDishes(LocalDate today) {
        return dishes().stream().filter(d -> d.isAvailable(today)).toList();
    }

    public boolean addRecipe(Recipe recipe) {
        List<Recipe> all = recipes();
        boolean duplicate = all.stream().anyMatch(r -> r.getName().equalsIgnoreCase(recipe.getName()));
        if (duplicate) return false;
        all.add(recipe);
        store.saveList(StorageKeys.RECIPES, all);
        return true;
    }

    public boolean addDish(Dish dish) {
        List<Dish> all = dishes();
        boolean duplicate = all.stream().anyMatch(d -> d.getName().equalsIgnoreCase(dish.getName()));
        if (duplicate) return false;
        all.add(dish);
        store.saveList(StorageKeys.DISHES, all);
        return true;
    }

    public boolean isMenuAcceptable(ThemedMenu menu) {
        return menu.workload() < config.getWorkloadPerPerson() * MENU_ACCEPTABILITY_FACTOR;
    }

    public boolean addThemedMenu(ThemedMenu menu) {
        if (menu == null || !isMenuAcceptable(menu)) return false;
        List<ThemedMenu> all = themedMenus();
        boolean duplicate = all.stream().anyMatch(m -> m.getName().equalsIgnoreCase(menu.getName()));
        if (duplicate) return false;
        all.add(menu);
        store.saveList(StorageKeys.THEMED_MENUS, all);
        return true;
    }
}
