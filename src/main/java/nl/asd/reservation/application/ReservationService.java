package nl.asd.reservation.application;

import nl.asd.reservation.ReservationNotFoundException;
import nl.asd.reservation.domain.*;
import nl.asd.shared.id.WorkplaceId;
import nl.asd.workplace.application.BuildingService;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservationService {
    private final ReservationRepository repository;
    private final BuildingService buildingService;

    public ReservationService(ReservationRepository repository, BuildingService buildingService) {
        this.repository = repository;
        this.buildingService = buildingService;
    }

    public ReservationId reserveWorkplace(WorkplaceId workplace, LocalDateTime from, LocalDateTime to) {
        // this.buildingService.openingHoursForBuilding(id, day)

        var id = this.repository.nextId();
        var timeslot = new Timeslot(from, to);
        var reservation = new Reservation(id, LocalDate.now(), ReservationType.ONCE, workplace);
        reservation.reserveTimeslot(timeslot, this.repository);

        this.repository.save(reservation);

        // Mag dit?
        return reservation.getId();
    }

    public void removeReservation(ReservationId reservation) {
        if (this.repository.ofId(reservation) == null)
            throw new ReservationNotFoundException("The reservation is not found!");

        this.repository.removeById(reservation);
    }
}