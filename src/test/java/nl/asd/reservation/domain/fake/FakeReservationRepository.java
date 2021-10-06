package nl.asd.reservation.domain.fake;

import nl.asd.reservation.domain.Reservation;
import nl.asd.reservation.domain.ReservationRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FakeReservationRepository implements ReservationRepository {
    private final HashMap<Long, Reservation> store = new HashMap<>();

    @Override
    public void clear() {
        store.clear();
    }

    @Override
    public List<Reservation> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void save(Reservation reservation) {
        store.put(reservation.getId(), reservation);
    }
}
