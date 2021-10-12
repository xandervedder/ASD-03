package nl.asd.workplace.domain;

import java.time.LocalTime;

public record OpeningTime(LocalTime from, LocalTime to) {
    public OpeningTime {
        if (from.isBefore(LocalTime.of(7, 0))) {
            throw new IllegalArgumentException("Openinghours cannot be before 07:00");
        }
        if (to.isBefore(LocalTime.of(18, 0))) {
            throw new IllegalArgumentException("Openinghours cannot be after 18:00");
        }
    }
}
