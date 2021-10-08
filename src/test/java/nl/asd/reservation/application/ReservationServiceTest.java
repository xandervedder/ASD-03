package nl.asd.reservation.application;

import nl.asd.reservation.ReservationNotFoundException;
import nl.asd.reservation.domain.ReservationId;
import nl.asd.reservation.domain.ReservationRepository;
import nl.asd.reservation.port.adapter.FakeReservationRepository;
import nl.asd.shared.id.WorkplaceId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReservationServiceTest {
    private ReservationService service;
    private ReservationRepository repository;

    private WorkplaceId workplace;
    private LocalDateTime from;
    private LocalDateTime to;

    @BeforeEach
    public void initialize() {
        this.repository = new FakeReservationRepository();
        this.service = new ReservationService(this.repository, null);

        this.workplace = new WorkplaceId(1L);
        this.from = LocalDateTime.of(2021, 12, 8, 12, 30);
        this.to = LocalDateTime.of(2021, 12, 8, 13, 0);
    }

    @Test
    public void shouldCreateReservationCorrectly() {
        assertDoesNotThrow(() -> this.service.reserveWorkplace(this.workplace, this.from, this.to));
    }

    @Test
    public void shouldRemoveReservationCorrectly() {
        var id = this.service.reserveWorkplace(this.workplace, this.from, this.to);
        this.service.cancelReservation(id);
        assertEquals(0, this.repository.findAll().size());
    }

    @Test
    public void removeReservationShouldNotThrowException() {
        var id = this.service.reserveWorkplace(this.workplace, this.from, this.to);
        assertDoesNotThrow(() -> this.service.cancelReservation(id));
    }

    @Test
    public void removeNonExistingReservationShouldThrowException() {
        assertThrows(ReservationNotFoundException.class, () -> this.service.cancelReservation(new ReservationId(1000L)));
    }
}
