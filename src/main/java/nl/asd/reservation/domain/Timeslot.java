package nl.asd.reservation.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

// https://docs.oracle.com/en/java/javase/14/language/records.html
public record Timeslot(LocalDateTime from, LocalDateTime to) {
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
