package nl.asd.reservation.application;

import nl.asd.reservation.port.adapter.FakeReservationRepository;
import nl.asd.shared.id.WorkplaceId;
import nl.asd.workplace.application.BuildingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReservationServiceTest {
    private ReservationService service;

    @BeforeEach
    public void initialize() {
        this.service = new ReservationService(new FakeReservationRepository(), null);
    }

    @Test
    public void shouldCreateReservationCorrectly() {
        var workplace = new WorkplaceId(1L);
        var from = LocalDateTime.of(2021, 12, 8, 12, 30);
        var to = LocalDateTime.of(2021, 12, 8, 13, 0);

        assertDoesNotThrow(() -> this.service.reserveWorkplace(workplace, from, to));
    }
}
