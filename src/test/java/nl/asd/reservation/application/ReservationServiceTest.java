package nl.asd.reservation.application;

import nl.asd.reservation.domain.*;
import nl.asd.reservation.port.adapter.FakeReservationRepository;
import nl.asd.shared.exception.CancellationNotAllowedException;
import nl.asd.shared.exception.ChangeTimeslotNotAllowedException;
import nl.asd.shared.exception.ReservationNotFoundException;
import nl.asd.shared.id.WorkplaceId;
import nl.asd.workplace.application.BuildingService;
import nl.asd.workplace.domain.Building;
import nl.asd.workplace.domain.OpeningTime;
import nl.asd.workplace.domain.Workplace;
import nl.asd.workplace.port.adapter.FakeBuildingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservationServiceTest {
    private ReservationService service;
    private ReservationRepository repository;

    private WorkplaceId workplace;
    private LocalDate reservationDate;
    private LocalTime from;
    private LocalTime to;

    private LocalTime from1;
    private LocalTime from2;
    private LocalTime from3;
    private LocalTime to1;
    private LocalTime to2;
    private LocalTime to3;

    /**
     * Helper method that reduces code bloat and increases readability
     *
     * @return Time normalized to zero minutes
     */
    private LocalTime time() {
        return LocalTime.now().withMinute(0);
    }

    @BeforeEach
    public void initialize() {
        var buildingRepository = new FakeBuildingRepository();
        var openingHours = new HashMap<DayOfWeek, OpeningTime>();
        openingHours.put(DayOfWeek.MONDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        openingHours.put(DayOfWeek.TUESDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        openingHours.put(DayOfWeek.WEDNESDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        openingHours.put(DayOfWeek.THURSDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        openingHours.put(DayOfWeek.FRIDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        openingHours.put(DayOfWeek.SATURDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        openingHours.put(DayOfWeek.SUNDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));

        var building = new Building(buildingRepository.nextId(), "Test Building", openingHours);
        building.registerWorkplace(new Workplace(new WorkplaceId(1L), 1, 1));
        building.registerWorkplace(new Workplace(new WorkplaceId(2L), 2, 1));
        buildingRepository.save(building);

        this.workplace = new WorkplaceId(1L);
        this.reservationDate = LocalDate.now().plusDays(1);
        this.from = LocalTime.of(12, 30);
        this.to = LocalTime.of(13, 0);

        this.from1 = this.from.plusMinutes(90);
        this.from2 = this.from.plusMinutes(120);
        this.from3 = this.from.plusMinutes(150);
        this.to1 = this.to.plusMinutes(90);
        this.to2 = this.to.plusMinutes(120);
        this.to3 = this.to.plusMinutes(150);

        this.repository = new FakeReservationRepository();
        this.service = new ReservationService(this.repository, new BuildingService(buildingRepository));
    }

    @Test
    public void shouldCreateReservationCorrectly() {
        assertDoesNotThrow(() -> this.service.reserveWorkplace(this.workplace, this.reservationDate, List.of(new Timeslot(this.from, this.to))));
    }

    @Test
    public void shouldCancelReservationCorrectly() {
        var id = this.service.reserveWorkplace(this.workplace, this.reservationDate, List.of(new Timeslot(this.from, this.to)));
        this.service.cancelReservation(id);
        assertEquals(0, this.repository.findAll().size());
    }

    @Test
    public void cancelReservationShouldNotThrowException() {
        var id = this.service.reserveWorkplace(this.workplace, this.reservationDate, List.of(new Timeslot(this.from, this.to)));
        assertDoesNotThrow(() -> this.service.cancelReservation(id));
    }

    @Test
    public void cancelNonExistingReservationShouldThrowException() {
        assertThrows(ReservationNotFoundException.class, () -> this.service.cancelReservation(new ReservationId(1000L)));
    }

    @Test
    public void cancelReservationTooLateShouldThrowException() {
        var id = this.service.reserveWorkplace(this.workplace, LocalDate.now(), List.of(new Timeslot(this.from, this.to)));
        assertThrows(CancellationNotAllowedException.class, () -> this.service.cancelReservation(id));
    }

    @Test
    public void shouldThrowBecauseOfIncompatibleTimeRange() {
        var workplace = new WorkplaceId(1L);
        var from = LocalTime.of(19, 30);
        var to = LocalTime.of(20, 0);

        assertThrows(RuntimeException.class, () -> this.service.reserveWorkplace(workplace, LocalDate.now(), List.of(new Timeslot(from, to))));
    }

    @Test
    public void shouldChangeTimeslotsCorrectly() {
        var id = this.service.reserveWorkplace(this.workplace, this.reservationDate, List.of(new Timeslot(this.from, this.to)));

        var newTimeslot1 = new Timeslot(this.from1, this.to1);
        var newTimeslot2 = new Timeslot(this.from2, this.to2);
        var newTimeslot3 = new Timeslot(this.from3, this.to3);

        this.service.changeTimeslotForExistingReservation(id, List.of(newTimeslot1, newTimeslot2, newTimeslot3));
        assertEquals(List.of(newTimeslot1, newTimeslot2, newTimeslot3), repository.ofId(id).getSlots());
    }

    @Test
    public void shouldChangeWorkplaceCorrectly() {
        var reservation = new Reservation(new ReservationId(1L), LocalDate.now(), ReservationType.ONCE, new WorkplaceId(1L));
        reservation.reserveTimeslot(new Timeslot(time(), time().plusMinutes(30)), this.repository);
        this.repository.save(reservation);

        this.service.migrateReservationToNewWorkplace(reservation.getId(), new WorkplaceId(2));

        assertEquals(new WorkplaceId(2), this.repository.ofId(reservation.getId()).getWorkplace());
    }

    @Test
    public void shouldThrowWhenNewWorkplaceIdIsEqualToCurrentWorkspaceId() {
        var reservation = new Reservation(new ReservationId(1L), LocalDate.now(), ReservationType.ONCE, new WorkplaceId(1L));
        reservation.reserveTimeslot(new Timeslot(time(), time().plusMinutes(30)), this.repository);
        this.repository.save(reservation);

        assertThrows(RuntimeException.class, () -> this.service.migrateReservationToNewWorkplace(reservation.getId(), new WorkplaceId(1)));
    }

    @Test
    public void shouldThrowWhenNewWorkplaceHasConflictingReservation() {
        var reservation = new Reservation(new ReservationId(1L), LocalDate.now(), ReservationType.ONCE, new WorkplaceId(1L));
        reservation.reserveTimeslot(new Timeslot(time(), time().plusMinutes(30)), this.repository);
        this.repository.save(reservation);

        var reservation2 = new Reservation(new ReservationId(2L), LocalDate.now(), ReservationType.ONCE, new WorkplaceId(2L));
        reservation2.reserveTimeslot(new Timeslot(time(), time().plusMinutes(30)), this.repository);
        this.repository.save(reservation2);

        assertThrows(RuntimeException.class, () -> this.service.migrateReservationToNewWorkplace(reservation.getId(), new WorkplaceId(2)));
    }

    @Test
    public void shouldThrowWhenChangingATimeslotOnTheDayOfTheReservation() {
        var id = this.service.reserveWorkplace(this.workplace, LocalDate.now(), List.of(new Timeslot(this.from, this.to)));

        var newTimeslot1 = new Timeslot(this.from1, this.to1);
        var newTimeslot2 = new Timeslot(this.from2, this.to2);
        var newTimeslot3 = new Timeslot(this.from3, this.to3);

        assertThrows(ChangeTimeslotNotAllowedException.class, () -> this.service.changeTimeslotForExistingReservation(id, List.of(newTimeslot1, newTimeslot2, newTimeslot3)));
    }

    @Test
    public void changeTimeslotOfNonExistingReservationShouldThrowException() {
        var newTimeslot1 = new Timeslot(this.from1, this.to1);
        var newTimeslot2 = new Timeslot(this.from2, this.to2);
        var newTimeslot3 = new Timeslot(this.from3, this.to3);

        assertThrows(ReservationNotFoundException.class, () -> this.service.changeTimeslotForExistingReservation(new ReservationId(1000L), List.of(newTimeslot1, newTimeslot2, newTimeslot3)));
    }
}
