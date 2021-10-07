package nl.asd.reservation.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TimeslotTest {
    @Test
    public void shouldThrowWhenTimeslotIsOverMultipleDays() {
        var now = LocalDate.now().atStartOfDay(); // 0:00 (full hour)
        assertThrows(RuntimeException.class, () -> new Timeslot(now, now.plusDays(1)));
    }

    @Test
    public void shouldThrowWhenTimeslotIsNot30Minutes() {
        var now = LocalDate.now().atStartOfDay(); // 0:00 (full hour)
        assertThrows(RuntimeException.class, () -> new Timeslot(now, now.plusMinutes(15)));
    }

    @Test
    public void shouldNotThrowWhenTimeslotIs30Minutes() {
        var now = LocalDate.now().atStartOfDay(); // 0:00 (full hour)
        assertDoesNotThrow(() -> new Timeslot(now, now.plusMinutes(30)));
    }

    @Test
    public void shouldThrowWhenTimeslotDoesNotStartAtFullHourOrHalfHour() {
        var now = LocalDateTime.now().withMinute(15);
        assertThrows(RuntimeException.class, () -> new Timeslot(now, now.plusMinutes(30)));
    }
}
