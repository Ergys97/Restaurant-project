package it.restaurant.api.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.restaurant.api.RestaurantApiApplication;
import it.restaurant.api.demo.DemoResetService;
import it.restaurant.api.dto.ConfigUpdateRequest;
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
class ConfigControllerTest {

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

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        resetService.reset();
    }

    @Test
    void getConfigReturns200() throws Exception {
        mockMvc.perform(get("/api/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seats").value(25))
                .andExpect(jsonPath("$.workloadPerPerson").value(2.0))
                .andExpect(jsonPath("$.initialized").value(true));
    }

    @Test
    void updateConfigWithValidDataReturns200() throws Exception {
        ConfigUpdateRequest request = new ConfigUpdateRequest();
        request.setSeats(30);
        request.setWorkloadPerPerson(2.5);

        mockMvc.perform(put("/api/config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seats").value(30))
                .andExpect(jsonPath("$.workloadPerPerson").value(2.5));
    }

    @Test
    void updateConfigRejectsNegativePerCapitaValues() throws Exception {
        String json = """
            {
                "seats": 30,
                "workloadPerPerson": 2.5,
                "perCapitaDrinks": {
                    "acqua": -1
                }
            }
        """;

        mockMvc.perform(put("/api/config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void updateConfigWithInvalidDataReturns400() throws Exception {
        ConfigUpdateRequest request = new ConfigUpdateRequest();
        request.setWorkloadPerPerson(2.5);

        mockMvc.perform(put("/api/config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }
}
