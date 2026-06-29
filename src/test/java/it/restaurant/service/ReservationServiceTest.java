package it.restaurant.service;

import static org.junit.jupiter.api.Assertions.*;

import it.restaurant.model.*;
import it.restaurant.repository.InMemoryStore;
import it.restaurant.repository.StorageKeys;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReservationServiceTest {

    private static final LocalDate DAY = LocalDate.of(2026, 7, 1);
    private ReservationService service;
    private InMemoryStore store;
    private Dish dish;

    @BeforeEach
    void setUp() {
        RestaurantConfig config = new RestaurantConfig(20, 5.0,
                List.of(), List.of(), List.of(), Map.of(), Map.of());
        store = new InMemoryStore();
        store.saveList(StorageKeys.INGREDIENTS, new ArrayList<>(List.of(new Ingredient("salame", 100, DAY))));
        store.saveList(StorageKeys.DRINKS, new ArrayList<>());
        store.saveList(StorageKeys.EXTRA_GOODS, new ArrayList<>());
        service = new ReservationService(config, store);
        Recipe recipe = new Recipe("salame", List.of(new Ingredient("salame", 15, DAY)), 0.4, 1.0, 10);
        dish = new Dish(recipe, DAY.plusDays(10));
    }

    private Reservation reservationOf(int covers, int dishQty) {
        Reservation r = new Reservation(DAY, covers);
        r.addDishOrder(dish, dishQty);
        return r;
    }

    @Test
    void dayWorkloadSumsReservationsOfThatDay() {
        List<Reservation> existing = List.of(reservationOf(1, 1), reservationOf(1, 1));
        assertEquals(0.8, service.dayWorkload(DAY, existing), 1e-9);
    }

    @Test
    void reservationWithinSustainableWorkloadIsAccepted() {
        List<Reservation> existing = List.of(reservationOf(1, 1), reservationOf(1, 1));
        assertTrue(service.isSustainable(reservationOf(1, 1), existing));
    }

    @Test
    void reservationExceedingSustainableWorkloadIsRejected() {
        assertFalse(service.isSustainable(reservationOf(1, 301), new ArrayList<>()));
    }

    @Test
    void availableSeatsSubtractsCoversOfSameDay() {
        List<Reservation> existing = List.of(reservationOf(8, 1), reservationOf(5, 1));
        assertEquals(7, service.availableSeats(DAY, existing));
        assertEquals(20, service.availableSeats(DAY.plusDays(1), existing));
    }

    @Test
    void confirmNotifiesObserversOnlyWhenAcceptable() {
        List<Reservation> notified = new ArrayList<>();
        service.addObserver(notified::add);

        assertTrue(service.createReservation(reservationOf(2, 1), new ArrayList<>()).isPresent());
        assertEquals(1, notified.size());

        assertFalse(service.createReservation(null, new ArrayList<>()).isPresent());
        assertFalse(service.createReservation(reservationOf(1, 301), new ArrayList<>()).isPresent());
        assertEquals(1, notified.size());
    }

    @Test
    void createReservationRejectsWhenCoversExceedAvailableSeats() {
        Reservation existing = reservationOf(19, 1);
        store.saveList(StorageKeys.RESERVATIONS, new ArrayList<>(List.of(existing)));

        Optional<Reservation> created = service.createReservation(reservationOf(2, 1));

        assertTrue(created.isEmpty());
        assertEquals(1, store.loadList(StorageKeys.RESERVATIONS, Reservation.class).size());
    }
}
