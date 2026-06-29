package it.restaurant.api.web;

import it.restaurant.api.dto.ErrorResponse;
import it.restaurant.api.dto.ReservationRequest;
import it.restaurant.model.Dish;
import it.restaurant.model.Reservation;
import it.restaurant.model.ThemedMenu;
import it.restaurant.repository.DataStore;
import it.restaurant.service.KitchenService;
import it.restaurant.service.ReservationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final DataStore store;
    private final KitchenService kitchenService;

    public ReservationController(ReservationService reservationService, DataStore store, KitchenService kitchenService) {
        this.reservationService = reservationService;
        this.store = store;
        this.kitchenService = kitchenService;
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> listReservations() {
        reservationService.cleanExpired();
        return ResponseEntity.ok(reservationService.listReservations());
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@Valid @RequestBody ReservationRequest request) {
        reservationService.cleanExpired();
        Reservation reservation = new Reservation(request.getDate(), request.getCovers());

        if (request.getDishOrders() != null) {
            List<Dish> dishes = kitchenService.dishes();
            for (var order : request.getDishOrders()) {
                Dish dish = dishes.stream()
                        .filter(d -> d.getName().equalsIgnoreCase(order.getDishName()))
                        .findFirst()
                        .orElse(null);
                if (dish == null) {
                    return ResponseEntity.badRequest()
                            .body("Dish not found: " + order.getDishName());
                }
                reservation.addDishOrder(dish, order.getQuantity());
            }
        }

        if (request.getMenuOrders() != null) {
            List<ThemedMenu> menus = kitchenService.themedMenus();
            for (var order : request.getMenuOrders()) {
                ThemedMenu menu = menus.stream()
                        .filter(m -> m.getName().equalsIgnoreCase(order.getMenuName()))
                        .findFirst()
                        .orElse(null);
                if (menu == null) {
                    return ResponseEntity.badRequest()
                            .body("Menu not found: " + order.getMenuName());
                }
                reservation.addMenuOrder(menu, order.getQuantity());
            }
        }

        List<Reservation> existing = reservationService.listReservations();
        var created = reservationService.createReservation(reservation, existing);
        if (created.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(created.get());
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(409, "Reservation cannot be accepted"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable String id) {
        boolean removed = reservationService.cancelReservation(id);
        if (!removed) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
