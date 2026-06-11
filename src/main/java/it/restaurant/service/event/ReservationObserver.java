package it.restaurant.service.event;

import it.restaurant.model.Reservation;

@FunctionalInterface
public interface ReservationObserver {
    void onReservationConfirmed(Reservation reservation);
}
