package nl.asd.reservation.port.adapter;

import nl.asd.reservation.domain.Reservation;
import nl.asd.reservation.domain.ReservationId;
import nl.asd.reservation.domain.ReservationRepository;
import nl.asd.shared.id.WorkplaceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class FakeReservationRepository implements ReservationRepository {
    private final HashMap<ReservationId, Reservation> store = new HashMap<>();
    private long internalIncrementor = 0L;

    @Override
    public Reservation ofId(ReservationId id) {
        return this.store.get(id);
    }

    @Override
    public ReservationId nextId() {
        this.internalIncrementor++;
        return new ReservationId(this.internalIncrementor);
    }

    @Override
    public List<Reservation> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Reservation> findByWorkplace(WorkplaceId id) {
        return this.store.values().stream()
                .filter(reservation -> reservation.getWorkplace().equals(id))
                .collect(Collectors.toList());
    }

    @Override
    public void save(Reservation reservation) {
        store.put(reservation.getId(), reservation);
    }

    @Override
    public void removeById(ReservationId id) {
        store.remove(id);
    }
}
