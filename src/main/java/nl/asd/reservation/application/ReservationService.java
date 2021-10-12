package nl.asd.reservation.application;

import nl.asd.reservation.domain.*;
import nl.asd.shared.exception.CancellationNotAllowedException;
import nl.asd.shared.exception.ReservationNotFoundException;
import nl.asd.shared.exception.WorkplaceNotFoundException;
import nl.asd.shared.id.WorkplaceId;
import nl.asd.workplace.application.BuildingService;

import java.time.LocalDate;
import java.util.List;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {
    private final ReservationRepository repository;
    private final BuildingService buildingService;

    public ReservationService(ReservationRepository repository, BuildingService buildingService) {
        this.repository = repository;
        this.buildingService = buildingService;
    }

    public ReservationId reserveWorkplace(WorkplaceId workplace, LocalDate reservationDate, List<Timeslot> timeslots) {
        // Check for each time range if it is outside the openings hours
        for (var slot : timeslots) {
            // Throw here or at buildingService...?
            if (this.buildingService.isTimeOutsideOfOpeningHoursForGivenDay(workplace, reservationDate, slot.from(), slot.to())) {
                throw new RuntimeException("Given time is not within opening hours range");
            }
        }

        var id = this.repository.nextId();
        var reservation = new Reservation(id, reservationDate, ReservationType.ONCE, workplace);
        reservation.reserveTimeslots(timeslots, this.repository);
        this.repository.save(reservation);

        return reservation.getId();
    }

    public void changeTimeslotForExistingReservation(ReservationId reservationId, List<Timeslot> newTimeslots) {
        var reservation = this.repository.ofId(reservationId);
        if (reservation == null) {
            throw new ReservationNotFoundException("The reservation is not found");
        }
        reservation.changeTimeslot(newTimeslots, this.repository);

        this.repository.save(reservation);
    }

    public void cancelReservation(ReservationId reservationId) {
        var reservation = this.repository.ofId(reservationId);
        if (reservation == null) {
            throw new ReservationNotFoundException("The reservation is not found");
        }

        if (!reservation.isCancellationAllowed(LocalDate.now())) {
            throw new CancellationNotAllowedException("Reservation cannot be cancelled on the same day");
        }

        this.repository.delete(reservation);
    }

    public ReservationId migrateReservationToNewWorkplace(ReservationId id, WorkplaceId newWorkplaceId) {
        var reservation = this.repository.ofId(id);

        if (!this.buildingService.doesWorkplaceExist(newWorkplaceId)) {
            throw new WorkplaceNotFoundException("Workplace must exist");
        }

        reservation.migrateTo(newWorkplaceId, repository);

        return reservation.getId();
    }
}
