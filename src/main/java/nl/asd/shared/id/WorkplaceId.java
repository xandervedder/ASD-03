package nl.asd.shared.id;

import java.util.Objects;

// https://docs.oracle.com/en/java/javase/14/language/records.html
public record WorkplaceId(long id) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkplaceId that = (WorkplaceId) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
