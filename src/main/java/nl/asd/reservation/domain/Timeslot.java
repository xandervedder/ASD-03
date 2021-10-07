package nl.asd.reservation.domain;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// https://docs.oracle.com/en/java/javase/14/language/records.html
public record Timeslot(LocalTime from, LocalTime to) {
    public Timeslot {
        if (ChronoUnit.MINUTES.between(from, to) != 30) {
            throw new RuntimeException("Timeslot should be 30 minutes");
        }

        if (isFullHourOrHalfHour(from, to)) {
            throw new RuntimeException("Timeslot should start at full hour or half hour");
        }
    }

    public long minutes() {
        return ChronoUnit.MINUTES.between(this.from, this.to);
    }

    // Checks if this slot conflicts with a number of different timeslots
    public boolean conflictsWith(List<Timeslot> others) {
        for(var slot : others) {
            if(this.conflictsWith(slot)) {
                return true;
            }
        }
        return false;
    }

    public boolean conflictsWith(Timeslot other) {
        return
                // begindatum huidige voor andere einddatum
                this.from.isBefore(other.to) &&
                        // begindatum andere voor huidige einddatum
                        other.from.isBefore(this.to);
    }

    public static List<Timeslot> ofTimeRange(LocalTime from, LocalTime to) {
        // Not actually used, but it will stay in the codebase to serve as an example
        if (from.equals(to)) {
            throw new IllegalArgumentException("A timerange has to exist");
        }

        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Start cannot be after end");
        }

        if (isFullHourOrHalfHour(from, to)) {
            throw new IllegalArgumentException("Given time range should be at full hour or half hour");
        }

        // Starting reference
        var fromHour = from.getHour();
        var hourDifference = to.getHour() - fromHour;
        // Edge cases that handles times like 11:30 - 12:00, we only want
        // to use the minutes in this case so we set the hourDifference to zero
        if (from.getMinute() == 30 && hourDifference == 1 ) {
            hourDifference = 0;
        }

        var minutes = from.getMinute() + to.getMinute();
        // Check how many slots need to be generated based on hours and minutes
        var slotCount = ((hourDifference * 60) + minutes) / 30;
        var timeslots = new ArrayList<Timeslot>(slotCount);

        // When the slotcount is zero, we can assume that there is only
        // one timeslot needed to be created
        if (slotCount == 0) {
            timeslots.add(new Timeslot(from, to));
            return timeslots;
        }

        for (int i = 0; i < slotCount; i++) {
            var newFrom = LocalTime.of(fromHour, from.getMinute()).plusMinutes(i * 30);
            // To is always 30 minutes later than the from, so we add an extra 1 to i
            var newTo = LocalTime.of(fromHour, from.getMinute()).plusMinutes((i + 1) * 30);
            timeslots.add(new Timeslot(newFrom, newTo));
        }

        return timeslots;
    }

    private static boolean isFullHourOrHalfHour(LocalTime from, LocalTime to) {
        var fromMinute = from.getMinute();
        var toMinute = to.getMinute();
        return !(fromMinute == 0 || fromMinute == 30) || !(toMinute == 0 || toMinute == 30);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Timeslot timeslot = (Timeslot) o;
        return from.equals(timeslot.from) && to.equals(timeslot.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
