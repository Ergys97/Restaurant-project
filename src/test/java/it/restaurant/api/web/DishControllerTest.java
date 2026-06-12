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
class DishControllerTest {

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
    void listDishesReturnsDishesAfterReset() throws Exception {
        mockMvc.perform(get("/api/dishes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Insalata Caprese"))
                .andExpect(jsonPath("$[0].available").isBoolean())
                .andExpect(jsonPath("$[1].name").value("Pizza Margherita"))
                .andExpect(jsonPath("$[1].available").isBoolean());
    }

    @Test
    void listMenusReturnsMenusAfterReset() throws Exception {
        mockMvc.perform(get("/api/menus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Menu Italiano"))
                .andExpect(jsonPath("$[0].dishCount").value(2))
                .andExpect(jsonPath("$[0].available").isBoolean());
    }
}
