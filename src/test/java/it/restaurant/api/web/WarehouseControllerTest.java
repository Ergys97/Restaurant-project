package it.restaurant.api.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.restaurant.api.RestaurantApiApplication;
import it.restaurant.api.demo.DemoResetService;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = RestaurantApiApplication.class, properties = {
        "restaurant.demo.enabled=false",
        "restaurant.demo.reset-enabled=false"
})
@AutoConfigureMockMvc
class WarehouseControllerTest {

    @TempDir
    static Path dataDir;

    @DynamicPropertySource
    static void apiProperties(DynamicPropertyRegistry registry) {
        registry.add("restaurant.data-dir", () -> dataDir.toString());
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DemoResetService resetService;

    @BeforeEach
    void setUp() {
        resetService.reset();
    }

    @Test
    void getWarehouseReturns200() throws Exception {
        mockMvc.perform(get("/api/warehouse"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ingredients").isArray())
                .andExpect(jsonPath("$.drinks").isArray())
                .andExpect(jsonPath("$.extraGoods").isArray());
    }
}
