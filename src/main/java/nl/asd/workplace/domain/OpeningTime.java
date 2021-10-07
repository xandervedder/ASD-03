package nl.asd.workplace.domain;

import java.time.LocalTime;

public record OpeningTime(LocalTime from, LocalTime to) {
}
