package it.restaurant.api.config;

import it.restaurant.model.RestaurantConfig;
import it.restaurant.repository.DataStore;
import it.restaurant.repository.DataStoreTransaction;
import it.restaurant.repository.JsonDataStore;
import it.restaurant.repository.StorageKeys;
import it.restaurant.service.KitchenService;
import it.restaurant.service.ReservationService;
import it.restaurant.service.WarehouseService;
import it.restaurant.service.event.ReservationNotifier;
import it.restaurant.service.event.ReservationObserver;
import it.restaurant.service.event.ReservationRegistry;
import it.restaurant.service.event.ShoppingListUpdater;
import it.restaurant.service.event.StockUpdater;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestaurantBeans {

    @Bean
    DataStore dataStore(RestaurantProperties properties) {
        return new JsonDataStore(properties.getDataDir());
    }

    @Bean
    RestaurantConfig restaurantConfig(DataStore store) {
        return store.load(StorageKeys.RESTAURANT_CONFIG, RestaurantConfig.class)
                .orElseGet(RestaurantConfig::new);
    }

    @Bean
    ReservationNotifier reservationNotifier() {
        return new ReservationNotifier();
    }

    @Bean
    ReservationObserver stockUpdater(DataStore store, RestaurantConfig config) {
        return new StockUpdater(store, config);
    }

    @Bean
    ReservationObserver shoppingListUpdater(DataStore store, RestaurantConfig config) {
        return new ShoppingListUpdater(store, config);
    }

    @Bean
    ReservationObserver reservationRegistry(DataStore store, ReservationNotifier notifier) {
        return new ReservationRegistry(store, notifier);
    }

    @Bean
    DataStoreTransaction dataStoreTransaction() {
        return new DataStoreTransaction();
    }

    @Bean
    ReservationService reservationService(RestaurantConfig config, DataStore store, DataStoreTransaction transaction, List<ReservationObserver> observers) {
        ReservationService service = new ReservationService(config, store, transaction);
        observers.forEach(service::addObserver);
        return service;
    }

    @Bean
    KitchenService kitchenService(DataStore store, RestaurantConfig config, DataStoreTransaction transaction) {
        return new KitchenService(store, config, transaction);
    }

    @Bean
    WarehouseService warehouseService(DataStore store, DataStoreTransaction transaction) {
        return new WarehouseService(store, transaction);
    }
}
