package nl.asd.reservation.domain;

import nl.asd.shared.id.WorkplaceId;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository {
    Reservation ofId(ReservationId id);

    ReservationId nextId();

    List<Reservation> findAll();

    List<Reservation> findByWorkplaceAndDate(WorkplaceId id, LocalDate date);

    void save(Reservation reservation);

    void delete(Reservation reservation);
}
