package nl.asd.workplace.domain;

import java.time.LocalTime;

public record OpeningHours(LocalTime from, LocalTime to) {
}
