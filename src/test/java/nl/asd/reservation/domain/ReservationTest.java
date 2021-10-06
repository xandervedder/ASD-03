package nl.asd.reservation.domain;

import nl.asd.reservation.domain.fake.FakeReservationRepository;
import nl.asd.workplace.domain.WorkplaceId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {
    private final ReservationRepository repository = new FakeReservationRepository();

    // reservering aanmaken
    // reservering annuleren
    // reservering wijzigen (andere Timeslot)
    // totale reserveringstijd ophalen

    @BeforeEach
    public void initialize() {
        var targetReservation = new Reservation(1L, LocalDate.now(), ReservationType.ONCE, new WorkplaceId(1L));
        targetReservation.reserveTimeslot(new Timeslot(LocalDateTime.now(), LocalDateTime.now().plusMinutes(30)), this.repository);
        this.repository.save(targetReservation);
        this.repository.save(new Reservation(2L, LocalDate.now(), ReservationType.ONCE, new WorkplaceId(2L)));
        this.repository.save(new Reservation(3L, LocalDate.now(), ReservationType.ONCE, new WorkplaceId(3L)));
        this.repository.save(new Reservation(4L, LocalDate.now(), ReservationType.ONCE, new WorkplaceId(4L)));
    }

    @AfterEach
    public void cleanUp() {
        // misschien handig om dit uiteindelijk niet zo te doen, maar een nieuwe instantie
        // van de repository aanmaken
        this.repository.clear();
    }

    @Test
    public void shouldCreateReservationCorrectly() {
    }

    @Test
    public void shouldCancelReservationCorectly() {
    }

    @Test
    public void shouldChangeReservationCorrectly() {
    }

    @Test
    public void shouldShowTotalReservationTimeCorrectlyWithOneTimeslot() {
        var reservation = new Reservation(0L, LocalDate.now(), ReservationType.ONCE, new WorkplaceId(5));
        var timeslot = new Timeslot(LocalDateTime.now(), LocalDateTime.now().plusMinutes(30));
        reservation.reserveTimeslot(timeslot, this.repository);

        assertEquals(30, reservation.totalMinutesReserved());
    }

    @Test
    public void shouldShowTotalReservationTimeCorrectlyWithMultipleTimeslots() {
        var reservation = new Reservation(0L, LocalDate.now(), ReservationType.ONCE, new WorkplaceId(5));
        var timeslot1 = new Timeslot(LocalDateTime.now(), LocalDateTime.now().plusMinutes(30));
        var timeslot2 = new Timeslot(LocalDateTime.now().plusMinutes(30), LocalDateTime.now().plusMinutes(60));
        var timeslot3 = new Timeslot(LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(90));

        reservation.reserveTimeslots(List.of(timeslot1, timeslot2, timeslot3), this.repository);

        assertEquals(90, reservation.totalMinutesReserved());
    }

    @Test
    public void shouldThrowWhenReservingDuplicateTimeslots() {
        var reservation = new Reservation(1L, LocalDate.now(), ReservationType.ONCE, new WorkplaceId(5));
        var timeslot = new Timeslot(LocalDateTime.now(), LocalDateTime.now().plusMinutes(30));

        reservation.reserveTimeslot(timeslot, this.repository);

        assertThrows(RuntimeException.class, () -> reservation.reserveTimeslot(timeslot, this.repository));
    }

    @Test
    public void shouldThrowWhenReservingATimeslotThatIsAlreadyInUse() {
        // workplaceid 1 is al in gebruik
        var reservation = new Reservation(1L, LocalDate.now(), ReservationType.ONCE, new WorkplaceId(1));

        assertThrows(RuntimeException.class, () -> reservation.reserveTimeslot(new Timeslot(LocalDateTime.now(), LocalDateTime.now().plusMinutes(30)), this.repository));
    }

    @Test
    public void shouldNotThrowWhenReservingInATimeslotAfterOtherTimeslot() {
        // workplaceid 1 is al in gebruik, timeslot niet
        var reservation = new Reservation(1L, LocalDate.now(), ReservationType.ONCE, new WorkplaceId(1));

        assertDoesNotThrow(() -> reservation.reserveTimeslot(new Timeslot(LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(90)), this.repository));
    }

    @Test
    public void shouldNotThrowWhenReservingInATimeslotBeforeOtherTimeslot() {
        // workplaceid 1 is al in gebruik, timeslot niet
        var reservation = new Reservation(1L, LocalDate.now(), ReservationType.ONCE, new WorkplaceId(1));

        assertDoesNotThrow(() -> reservation.reserveTimeslot(new Timeslot(LocalDateTime.now().minusMinutes(90), LocalDateTime.now().minusMinutes(60)), this.repository));
    }
}