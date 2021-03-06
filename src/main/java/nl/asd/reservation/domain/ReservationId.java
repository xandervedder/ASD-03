package nl.asd.reservation.domain;

import java.util.Objects;

// https://docs.oracle.com/en/java/javase/14/language/records.html
public record ReservationId(long id) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationId that = (ReservationId) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
