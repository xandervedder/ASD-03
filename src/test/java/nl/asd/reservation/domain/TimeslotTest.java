package nl.asd.reservation.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.Stream;

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

    @Test
    public void shouldThrowWhenGivingTheSameTimes() {
        var now = LocalTime.now().withMinute(0);
        assertThrows(IllegalArgumentException.class, () -> Timeslot.ofTimeRange(now, now));
    }

    @Test
    public void shouldThrowWhenFromIsHigherThanTo() {
        var from = LocalTime.now().withMinute(0).plusHours(1);
        var to = LocalTime.now().withMinute(0);
        assertThrows(IllegalArgumentException.class, () -> Timeslot.ofTimeRange(from, to));
    }

    @Test
    public void shouldThrowWhenGivingTimeRangeNotAtFullHourOrHalfHour() {
        var from = LocalTime.now().withMinute(0).plusMinutes(15);
        var to = LocalTime.now().withMinute(0).plusMinutes(45);
        assertThrows(IllegalArgumentException.class, () -> Timeslot.ofTimeRange(from, to));
    }

    @ParameterizedTest
    @MethodSource("timeslotProvider")
    public void shouldProduceCorrectAmountOfTimeslots(int expected, LocalTime from, LocalTime to) {
        assertEquals(expected, Timeslot.ofTimeRange(from, to).size());
    }

    private static Stream<Arguments> timeslotProvider() {
        var time = LocalTime.now().withMinute(0);
        return Stream.of(
                Arguments.of(1, time, time.plusMinutes(30)),
                Arguments.of(1, time.plusMinutes(30), time.plusHours(1)),
                Arguments.of(2, time, time.plusMinutes(60)),
                Arguments.of(3, time, time.plusMinutes(90)),
                Arguments.of(5, time, time.plusMinutes(150))
        );
    }

    @Test
    public void shouldProduceCorrectTimeslots() {
        var now = LocalTime.now().withMinute(0).withSecond(0).withNano(0);
        var comparedTimeslot = new Timeslot(now, now.plusMinutes(30));
        var timeslots = Timeslot.ofTimeRange(now, now.plusHours(1));
        assertEquals(comparedTimeslot, timeslots.get(0));
    }

    @Test
    public void shouldProduceCorrectTimeslot() {
        var now = LocalTime.now().withMinute(0).withSecond(0).withNano(0);
        var comparedTimeslot = new Timeslot(now, now.plusMinutes(30));
        var timeslots = Timeslot.ofTimeRange(now, now.plusMinutes(30));
        assertEquals(comparedTimeslot, timeslots.get(0));
    }
}
