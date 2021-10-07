package nl.asd.reservation.domain;

import nl.asd.shared.id.WorkplaceId;

import java.util.List;

public interface ReservationRepository {
    Reservation ofId(ReservationId id);
    ReservationId nextId();
    List<Reservation> findAll();
    List<Reservation> findByWorkplace(WorkplaceId id);
    void save(Reservation reservation);
}
