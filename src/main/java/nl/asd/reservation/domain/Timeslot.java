package nl.asd.reservation.domain;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

// https://docs.oracle.com/en/java/javase/14/language/records.html
public record Timeslot(LocalTime from, LocalTime to) {
    public Timeslot {
        if (!from.getDayOfWeek().equals(to.getDayOfWeek())) {
            throw new RuntimeException("Timeslot should be on the same day");
        }

        if (ChronoUnit.MINUTES.between(from, to) != 30) {
            throw new RuntimeException("Timeslot should be 30 minutes");
        }

        var fromMinute = from.getMinute();
        var toMinute = to.getMinute();
        if (!(fromMinute == 0 || fromMinute == 30) || !(toMinute == 0 || toMinute == 30)) {
            throw new RuntimeException("Timeslot should start at full hour or half hour");
        }
    }

    public long minutes() {
        return ChronoUnit.MINUTES.between(this.from, this.to);
    }

    public boolean conflictsWith(Timeslot other) {
        return
                // begindatum huidige voor andere einddatum
                this.from.isBefore(other.to) &&
                        // begindatum andere voor huidige einddatum
                        other.from.isBefore(this.to);
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
