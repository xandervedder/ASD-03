package nl.asd.reservation.application;

import nl.asd.reservation.ReservationNotFoundException;
import nl.asd.reservation.domain.ReservationId;
import nl.asd.reservation.port.adapter.FakeReservationRepository;
import nl.asd.shared.id.WorkplaceId;
import nl.asd.workplace.application.BuildingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReservationServiceTest {
    private ReservationService service;
    private FakeReservationRepository repository;

    @BeforeEach
    public void initialize() {
        this.repository = new FakeReservationRepository();
        this.service = new ReservationService(this.repository, null);
    }

    @Test
    public void shouldCreateReservationCorrectly() {
        var workplace = new WorkplaceId(1L);
        var from = LocalDateTime.of(2021, 12, 8, 12, 30);
        var to = LocalDateTime.of(2021, 12, 8, 13, 0);

        assertDoesNotThrow(() -> this.service.reserveWorkplace(workplace, from, to));
    }

    @Test
    public void shouldRemoveReservationCorrectly() {
        var workplace = new WorkplaceId(2L);
        var from = LocalDateTime.of(2021, 12, 8, 12, 30);
        var to = LocalDateTime.of(2021, 12, 8, 13, 0);
        var id = this.service.reserveWorkplace(workplace, from, to);
        this.service.removeReservation(id);

        assertEquals(0, this.repository.findAll().size());
    }

    @Test
    public void removeReservationShouldNotThrowException() {
        var workplace = new WorkplaceId(3L);
        var from = LocalDateTime.of(2021, 12, 8, 12, 30);
        var to = LocalDateTime.of(2021, 12, 8, 13, 0);
        var id = this.service.reserveWorkplace(workplace, from, to);

        assertDoesNotThrow(() -> this.service.removeReservation(id));
    }

    @Test
    public void removeNonExistingReservationShouldThrowException() {
        var workplace = new WorkplaceId(3L);
        var from = LocalDateTime.of(2021, 12, 8, 12, 30);
        var to = LocalDateTime.of(2021, 12, 8, 13, 0);
        var id = this.service.reserveWorkplace(workplace, from, to);

        assertThrows(ReservationNotFoundException.class, () -> this.service.removeReservation(new ReservationId(1000L)));
    }
}
