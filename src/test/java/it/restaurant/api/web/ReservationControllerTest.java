package it.restaurant.api.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import it.restaurant.api.RestaurantApiApplication;
import it.restaurant.api.demo.DemoResetService;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = RestaurantApiApplication.class, properties = {
        "restaurant.demo.enabled=false",
        "restaurant.demo.reset-enabled=false"
})
@AutoConfigureMockMvc
class ReservationControllerTest {

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
    void listReturnsReservations() throws Exception {
        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").isNotEmpty());
    }

    @Test
    void createWithValidDataReturns201() throws Exception {
        String json = """
            {
                "date": "2026-07-01",
                "covers": 4,
                "dishOrders": [
                    {"dishName": "Pizza Margherita", "quantity": 1}
                ]
            }
        """;
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.date").isNotEmpty())
                .andExpect(jsonPath("$.covers").value(4));
    }

    @Test
    void createWithoutCoversReturns400() throws Exception {
        String json = """
            { "date": "2026-07-01" }
        """;
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWithNonexistentDishReturns400() throws Exception {
        String json = """
            {
                "date": "2026-07-01",
                "covers": 4,
                "dishOrders": [
                    {"dishName": "Nonexistent Dish", "quantity": 2}
                ]
            }
        """;
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWithInvalidNestedDishOrderReturns400() throws Exception {
        String json = """
            {
                "date": "2026-07-01",
                "covers": 4,
                "dishOrders": [
                    {"dishName": "", "quantity": 0}
                ]
            }
        """;

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void deleteExistingReturns204() throws Exception {
        String response = mockMvc.perform(get("/api/reservations"))
                .andReturn().getResponse().getContentAsString();
        String id = JsonPath.read(response, "$[0].id");

        mockMvc.perform(delete("/api/reservations/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteNonExistentReturns404() throws Exception {
        mockMvc.perform(delete("/api/reservations/nonexistent-id"))
                .andExpect(status().isNotFound());
    }
}
