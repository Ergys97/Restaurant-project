package it.restaurant.api;

import static org.assertj.core.api.Assertions.assertThat;

import it.restaurant.repository.DataStore;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(properties = {
        "restaurant.demo.enabled=false",
        "restaurant.demo.reset-enabled=false"
})
class RestaurantApiApplicationTest {

    @TempDir
    static Path dataDir;

    @DynamicPropertySource
    static void apiProperties(DynamicPropertyRegistry registry) {
        registry.add("restaurant.data-dir", () -> dataDir.toString());
    }

    @Autowired
    private ApplicationContext context;

    @Test
    void loadsApiContextWithDataStoreBean() {
        assertThat(context.getBean(DataStore.class)).isNotNull();
    }
}
