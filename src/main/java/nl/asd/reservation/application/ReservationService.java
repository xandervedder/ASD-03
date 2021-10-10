package nl.asd.reservation.application;

import nl.asd.reservation.CancellationNotAllowedException;
import nl.asd.reservation.ReservationNotFoundException;
import nl.asd.reservation.domain.*;
import nl.asd.shared.id.WorkplaceId;
import nl.asd.workplace.application.BuildingService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservationService {
    private final ReservationRepository repository;
    private final BuildingService buildingService;

    public ReservationService(ReservationRepository repository, BuildingService buildingService) {
        this.repository = repository;
        this.buildingService = buildingService;
    }

    public ReservationId reserveWorkplace(WorkplaceId workplace, LocalDate reservationDate, List<LocalTime> from, List<LocalTime> to) {
        // Check for each time range if it is outside the openings hours
        for (int i = 0; i < from.size(); i++) {
            // Throw here or at buildingService...?
            if (this.buildingService.isTimeOutsideOfOpeningHoursForGivenDay(workplace, reservationDate, from.get(i), to.get(i))) {
                throw new RuntimeException("Given time is not within opening hours range");
            }
        }

        var id = this.repository.nextId();
        var reservation = new Reservation(id, reservationDate, ReservationType.ONCE, workplace);
        reservation.reserveTimeslots(from, to, this.repository);
        this.repository.save(reservation);

        // Mag dit?
        return reservation.getId();
    }

    public ReservationId reserveWorkplace(WorkplaceId workplace, LocalDate reservationDate, LocalTime from, LocalTime to) {
        return this.reserveWorkplace(workplace, reservationDate, List.of(from), List.of(to));
    }

    public void cancelReservation(ReservationId reservationId) {
        var reservation = this.repository.ofId(reservationId);
        if (reservation == null)
            throw new ReservationNotFoundException("The reservation is not found");

        if (!reservation.isCancellationAllowed(LocalDate.now()))
            throw new CancellationNotAllowedException("Reservation cannot be cancelled on the same day");

        this.repository.delete(reservation);
    }
}
