package nl.asd.reservation.application;

import nl.asd.reservation.CancellationNotAllowedException;
import nl.asd.reservation.ReservationNotFoundException;
import nl.asd.reservation.domain.*;
import nl.asd.shared.id.WorkplaceId;
import nl.asd.workplace.application.BuildingService;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservationService {
    private final ReservationRepository repository;
    private final BuildingService buildingService;

    public ReservationService(ReservationRepository repository, BuildingService buildingService) {
        this.repository = repository;
        this.buildingService = buildingService;
    }

    // TODO: Multiple timeslots from given time range
    public ReservationId reserveWorkplace(WorkplaceId workplace, LocalDate reservationDate, LocalTime from, LocalTime to) {
        // Throw here or at buildingService...?
        if (this.buildingService.isTimeOutsideOfOpeningHoursForGivenDay(workplace, reservationDate, from, to)) {
            throw new RuntimeException("Given time is not within opening hours range");
        }

        var id = this.repository.nextId();

        var reservation = new Reservation(id, reservationDate, ReservationType.ONCE, workplace);
        reservation.reserveTimeslot(from, to, this.repository);
        this.repository.save(reservation);

        // Mag dit?
        return reservation.getId();
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
