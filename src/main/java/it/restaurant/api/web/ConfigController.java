package it.restaurant.api.web;

import it.restaurant.api.dto.ConfigUpdateRequest;
import it.restaurant.model.RestaurantConfig;
import it.restaurant.repository.DataStore;
import it.restaurant.repository.StorageKeys;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    private final RestaurantConfig config;
    private final DataStore store;

    public ConfigController(RestaurantConfig config, DataStore store) {
        this.config = config;
        this.store = store;
    }

    @GetMapping
    public ResponseEntity<RestaurantConfig> getConfig() {
        return ResponseEntity.ok(config);
    }

    @PutMapping
    public ResponseEntity<RestaurantConfig> updateConfig(@Valid @RequestBody ConfigUpdateRequest request) {
        var ingredients = request.getIngredients() != null
                ? request.getIngredients() : config.getIngredients();
        var drinks = request.getDrinks() != null
                ? request.getDrinks() : config.getDrinks();
        var extraGoods = request.getExtraGoods() != null
                ? request.getExtraGoods() : config.getExtraGoods();
        Map<String, Integer> perCapitaDrinks = request.getPerCapitaDrinks() != null
                ? request.getPerCapitaDrinks() : config.getPerCapitaDrinks();
        Map<String, Integer> perCapitaExtraGoods = request.getPerCapitaExtraGoods() != null
                ? request.getPerCapitaExtraGoods() : config.getPerCapitaExtraGoods();

        RestaurantConfig updated = new RestaurantConfig(
                request.getSeats(), request.getWorkloadPerPerson(),
                ingredients, drinks, extraGoods,
                perCapitaDrinks, perCapitaExtraGoods);
        store.save(StorageKeys.RESTAURANT_CONFIG, updated);
        config.apply(updated);
        return ResponseEntity.ok(config);
    }
}
