package it.restaurant.api.demo;

import static org.assertj.core.api.Assertions.assertThat;

import it.restaurant.api.RestaurantApiApplication;
import it.restaurant.model.Dish;
import it.restaurant.model.Ingredient;
import it.restaurant.model.Recipe;
import it.restaurant.model.Reservation;
import it.restaurant.model.RestaurantConfig;
import it.restaurant.model.ShoppingList;
import it.restaurant.model.ThemedMenu;
import it.restaurant.repository.DataStore;
import it.restaurant.repository.StorageKeys;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(classes = RestaurantApiApplication.class, properties = {
        "restaurant.demo.enabled=false",
        "restaurant.demo.reset-enabled=true"
})
class DemoResetServiceTest {

    @TempDir
    static Path dataDir;

    @DynamicPropertySource
    static void apiProperties(DynamicPropertyRegistry registry) {
        registry.add("restaurant.data-dir", () -> dataDir.toString());
    }

    @Autowired
    private DemoResetService resetService;

    @Autowired
    private DataStore store;

    @Autowired
    private RestaurantConfig currentConfig;

    @Test
    void resetCreatesCoherentDemoDatasetAndRefreshesCurrentConfig() {
        DemoResetSummary summary = resetService.reset();

        RestaurantConfig savedConfig = store.load(StorageKeys.RESTAURANT_CONFIG, RestaurantConfig.class).orElseThrow();
        assertThat(savedConfig.isInitialized()).isTrue();
        assertThat(currentConfig.isInitialized()).isTrue();
        assertThat(currentConfig.getSeats()).isEqualTo(savedConfig.getSeats());

        assertThat(store.loadList(StorageKeys.INGREDIENTS, Ingredient.class)).isNotEmpty();
        assertThat(store.loadList(StorageKeys.RECIPES, Recipe.class)).isNotEmpty();
        assertThat(store.loadList(StorageKeys.DISHES, Dish.class)).isNotEmpty();
        assertThat(store.loadList(StorageKeys.THEMED_MENUS, ThemedMenu.class)).isNotEmpty();
        assertThat(store.loadList(StorageKeys.RESERVATIONS, Reservation.class))
                .extracting(Reservation::getId)
                .doesNotContainNull();
        assertThat(store.load(StorageKeys.SHOPPING_LIST, ShoppingList.class).orElseThrow().isEmpty()).isFalse();
        assertThat(summary.reservations()).isGreaterThan(0);
    }
}
