package nl.asd.reservation.domain;

import java.util.List;

public interface ReservationRepository {
    void clear();
    List<Reservation> findAll();
    void save(Reservation reservation);
}
