package nl.asd.reservation.application;

import nl.asd.reservation.CancellationNotAllowedException;
import nl.asd.reservation.ReservationNotFoundException;
import nl.asd.reservation.domain.ReservationId;
import nl.asd.reservation.domain.ReservationRepository;
import nl.asd.reservation.port.adapter.FakeReservationRepository;
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

import static org.junit.jupiter.api.Assertions.*;

class ReservationServiceTest {
    private ReservationService service;
    private ReservationRepository repository;

    private WorkplaceId workplace;
    private LocalDate reservationDate;
    private LocalTime from;
    private LocalTime to;

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
        buildingRepository.save(building);

        this.workplace = new WorkplaceId(1L);
        this.reservationDate = LocalDate.now().plusDays(1);
        this.from = LocalTime.of(12, 30);
        this.to = LocalTime.of(13, 0);

        this.repository = new FakeReservationRepository();
        this.service = new ReservationService(this.repository, new BuildingService(buildingRepository));
    }

    @Test
    public void shouldCreateReservationCorrectly() {
        assertDoesNotThrow(() -> this.service.reserveWorkplace(this.workplace, this.reservationDate, this.from, this.to));
    }

    @Test
    public void shouldCancelReservationCorrectly() {
        var id = this.service.reserveWorkplace(this.workplace, this.reservationDate, this.from, this.to);
        this.service.cancelReservation(id);
        assertEquals(0, this.repository.findAll().size());
    }

    @Test
    public void cancelReservationShouldNotThrowException() {
        var id = this.service.reserveWorkplace(this.workplace, this.reservationDate, this.from, this.to);
        assertDoesNotThrow(() -> this.service.cancelReservation(id));
    }

    @Test
    public void cancelNonExistingReservationShouldThrowException() {
        assertThrows(ReservationNotFoundException.class, () -> this.service.cancelReservation(new ReservationId(1000L)));
    }

    @Test
    public void cancelReservationTooLateShouldThrowException() {
        var id = this.service.reserveWorkplace(this.workplace, LocalDate.now(), this.from, this.to);
        assertThrows(CancellationNotAllowedException.class, () -> this.service.cancelReservation(id));
    }

    @Test
    public void shouldThrowBecauseOfIncompatibleTimeRange() {
        var workplace = new WorkplaceId(1L);
        var from = LocalTime.of(19, 30);
        var to = LocalTime.of(20, 0);

        assertThrows(RuntimeException.class, () -> this.service.reserveWorkplace(workplace, LocalDate.now(), from, to));
    }
}
