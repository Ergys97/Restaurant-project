package it.restaurant.api.web;

import it.restaurant.api.config.RestaurantProperties;
import it.restaurant.api.demo.DemoResetService;
import it.restaurant.api.demo.DemoResetSummary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/demo")
public class DemoController {

    private final DemoResetService resetService;
    private final RestaurantProperties properties;

    public DemoController(DemoResetService resetService, RestaurantProperties properties) {
        this.resetService = resetService;
        this.properties = properties;
    }

    @PostMapping("/reset")
    public ResponseEntity<DemoResetSummary> reset() {
        if (!properties.getDemo().isResetEnabled()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        DemoResetSummary summary = resetService.reset();
        return ResponseEntity.ok(summary);
    }
}
