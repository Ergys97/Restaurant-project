package it.restaurant.service.event;

import it.restaurant.model.Reservation;

public class ReservationNotifier implements ReservationObserver {

    private boolean pending = false;

    @Override
    public void onReservationConfirmed(Reservation reservation) { flag(); }

    void flag() { pending = true; }

    public boolean consumePending() {
        boolean was = pending;
        pending = false;
        return was;
    }
}
