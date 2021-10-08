package nl.asd.reservation.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class TimeslotTest {
    @Test
    public void shouldThrowWhenTimeslotIsNot30Minutes() {
        var now = LocalTime.now().withMinute(0); // 0:00 (full hour)
        assertThrows(RuntimeException.class, () -> new Timeslot(now, now.plusMinutes(15)));
    }

    @Test
    public void shouldNotThrowWhenTimeslotIs30Minutes() {
        var now = LocalTime.now().withMinute(30); // 0:00 (full hour)
        assertDoesNotThrow(() -> new Timeslot(now, now.plusMinutes(30)));
    }

    @Test
    public void shouldThrowWhenTimeslotDoesNotStartAtFullHourOrHalfHour() {
        var now = LocalTime.now().withMinute(15);
        assertThrows(RuntimeException.class, () -> new Timeslot(now, now.plusMinutes(30)));
    }
}
