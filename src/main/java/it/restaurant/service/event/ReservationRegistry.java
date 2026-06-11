package it.restaurant.service.event;

import it.restaurant.model.Reservation;
import it.restaurant.repository.DataStore;
import it.restaurant.repository.StorageKeys;
import java.time.LocalDate;
import java.util.List;

public class ReservationRegistry implements ReservationObserver {

    private final DataStore store;
    private final ReservationNotifier notifier;

    public ReservationRegistry(DataStore store, ReservationNotifier notifier) {
        this.store = store;
        this.notifier = notifier;
    }

    @Override
    public void onReservationConfirmed(Reservation reservation) {
        List<Reservation> all = store.loadList(StorageKeys.RESERVATIONS, Reservation.class);
        all.removeIf(r -> r.getDate().isBefore(LocalDate.now()));
        all.add(reservation);
        store.saveList(StorageKeys.RESERVATIONS, all);
        notifier.flag();
    }
}
