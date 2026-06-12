package it.restaurant.api.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.restaurant.api.RestaurantApiApplication;
import java.nio.file.Path;
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
class DemoControllerDisabledTest {

    @TempDir
    static Path dataDir;

    @DynamicPropertySource
    static void apiProperties(DynamicPropertyRegistry registry) {
        registry.add("restaurant.data-dir", () -> dataDir.toString());
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void resetWhenDisabledReturns403() throws Exception {
        mockMvc.perform(post("/api/admin/demo/reset"))
                .andExpect(status().isForbidden());
    }
}
