package nl.asd.reservation.application;

import nl.asd.reservation.ReservationNotFoundException;
import nl.asd.reservation.CancellationNotAllowedException;
import nl.asd.reservation.domain.ReservationId;
import nl.asd.reservation.domain.ReservationRepository;
import nl.asd.reservation.port.adapter.FakeReservationRepository;
import nl.asd.shared.id.WorkplaceId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ReservationServiceTest {
    private ReservationService service;
    private ReservationRepository repository;

    private WorkplaceId workplace;
    private LocalDate date;
    private LocalTime from;
    private LocalTime to;

    @BeforeEach
    public void initialize() {
        this.repository = new FakeReservationRepository();
        this.service = new ReservationService(this.repository, null);

        this.workplace = new WorkplaceId(1L);
        this.date = LocalDate.now().plusDays(1);
        this.from = LocalTime.of(12, 30);
        this.to = LocalTime.of(13, 0);
    }

    @Test
    public void shouldCreateReservationCorrectly() {
        assertDoesNotThrow(() -> this.service.reserveWorkplace(this.workplace, this.date, this.from, this.to));
    }

    @Test
    public void shouldCancelReservationCorrectly() {
        var id = this.service.reserveWorkplace(this.workplace, this.date, this.from, this.to);
        this.service.cancelReservation(id);
        assertEquals(0, this.repository.findAll().size());
    }

    @Test
    public void cancelReservationShouldNotThrowException() {
        var id = this.service.reserveWorkplace(this.workplace, this.date, this.from, this.to);
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
}
