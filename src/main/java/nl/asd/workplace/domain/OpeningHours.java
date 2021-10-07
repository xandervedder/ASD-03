package nl.asd.workplace.domain;

import java.time.LocalDateTime;

public record OpeningHours(Day day, LocalDateTime from, LocalDateTime to) {
}
