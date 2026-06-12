package it.restaurant.api.web;

import it.restaurant.api.dto.DishSummary;
import it.restaurant.api.dto.MenuSummary;
import it.restaurant.service.KitchenService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DishController {

    private final KitchenService kitchenService;

    public DishController(KitchenService kitchenService) {
        this.kitchenService = kitchenService;
    }

    @GetMapping("/api/dishes")
    public ResponseEntity<List<DishSummary>> listDishes() {
        var today = LocalDate.now();
        var dishes = kitchenService.dishes().stream()
                .map(d -> new DishSummary(d.getName(), d.isAvailable(today)))
                .toList();
        return ResponseEntity.ok(dishes);
    }

    @GetMapping("/api/menus")
    public ResponseEntity<List<MenuSummary>> listMenus() {
        var today = LocalDate.now();
        var menus = kitchenService.themedMenus().stream()
                .map(m -> new MenuSummary(m.getName(), m.getDishes().size(), !m.getAvailableUntil().isBefore(today)))
                .toList();
        return ResponseEntity.ok(menus);
    }
}
