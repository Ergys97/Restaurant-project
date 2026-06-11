package it.restaurant.service;

import it.restaurant.model.Reservation;
import it.restaurant.model.RestaurantConfig;
import it.restaurant.service.event.ReservationObserver;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {

    private final RestaurantConfig config;
    private final List<ReservationObserver> observers = new ArrayList<>();

    public ReservationService(RestaurantConfig config) { this.config = config; }

    public void addObserver(ReservationObserver observer) { observers.add(observer); }

    public int availableSeats(LocalDate date, List<Reservation> existing) {
        int taken = existing.stream()
                .filter(r -> r.getDate().equals(date))
                .mapToInt(Reservation::getCovers)
                .sum();
        return config.getSeats() - taken;
    }

    public double dayWorkload(LocalDate date, List<Reservation> reservations) {
        return reservations.stream()
                .filter(r -> r.getDate().equals(date))
                .mapToDouble(Reservation::workload)
                .sum();
    }

    public boolean isSustainable(Reservation candidate, List<Reservation> existing) {
        double total = dayWorkload(candidate.getDate(), existing) + candidate.workload();
        return total < config.getSustainableWorkload();
    }

    public boolean confirm(Reservation candidate, List<Reservation> existing) {
        if (candidate == null || !isSustainable(candidate, existing)) {
            return false;
        }
        observers.forEach(o -> o.onReservationConfirmed(candidate));
        return true;
    }
}
