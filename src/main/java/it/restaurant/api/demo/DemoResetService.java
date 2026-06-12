package it.restaurant.api.demo;

import it.restaurant.model.RestaurantConfig;
import it.restaurant.repository.DataStore;
import it.restaurant.repository.StorageKeys;
import it.restaurant.service.KitchenService;
import it.restaurant.service.WarehouseService;
import java.util.ArrayList;
import org.springframework.stereotype.Service;

@Service
public class DemoResetService {

    private final DataStore store;
    private final RestaurantConfig currentConfig;
    private final KitchenService kitchenService;

    public DemoResetService(DataStore store, RestaurantConfig currentConfig,
                            KitchenService kitchenService) {
        this.store = store;
        this.currentConfig = currentConfig;
        this.kitchenService = kitchenService;
    }

    public DemoResetSummary reset() {
        store.saveList(StorageKeys.RESERVATIONS, new ArrayList<>());
        store.saveList(StorageKeys.INGREDIENTS, new ArrayList<>());
        store.saveList(StorageKeys.DRINKS, new ArrayList<>());
        store.saveList(StorageKeys.EXTRA_GOODS, new ArrayList<>());
        store.saveList(StorageKeys.RECIPES, new ArrayList<>());
        store.saveList(StorageKeys.DISHES, new ArrayList<>());
        store.saveList(StorageKeys.THEMED_MENUS, new ArrayList<>());
        store.save(StorageKeys.SHOPPING_LIST, new it.restaurant.model.ShoppingList());
        store.save(StorageKeys.RESTAURANT_CONFIG, new RestaurantConfig());

        DemoDataSeeder seeder = new DemoDataSeeder(store);
        DemoResetSummary summary = seeder.seed();

        RestaurantConfig savedConfig = store.load(StorageKeys.RESTAURANT_CONFIG, RestaurantConfig.class)
                .orElseThrow();
        currentConfig.apply(savedConfig);
        kitchenService.setConfig(currentConfig);

        return summary;
    }
}
