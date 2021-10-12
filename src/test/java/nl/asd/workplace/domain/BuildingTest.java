package nl.asd.workplace.domain;

import nl.asd.shared.id.BuildingId;
import nl.asd.shared.id.WorkplaceId;
import nl.asd.workplace.port.adapter.FakeBuildingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class BuildingTest {
    private BuildingRepository buildingRepository;
    private HashMap<DayOfWeek, OpeningTime> standardOpeningHours;
    private Building building;

    @BeforeEach
    void initialize() {
        this.buildingRepository = new FakeBuildingRepository();

        this.standardOpeningHours = new HashMap<>();
        this.standardOpeningHours.put(DayOfWeek.MONDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        this.standardOpeningHours.put(DayOfWeek.TUESDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        this.standardOpeningHours.put(DayOfWeek.WEDNESDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        this.standardOpeningHours.put(DayOfWeek.THURSDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        this.standardOpeningHours.put(DayOfWeek.FRIDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));

        this.building = new Building(this.buildingRepository.nextBuildingId(), "Test Building", this.standardOpeningHours, new Address("1234AB", "Kerelman", 13, ""));
        this.buildingRepository.save(this.building);
    }

    @Test
    void shouldCreateBuildingCorrectly() {
        assertDoesNotThrow(() -> new Building(new BuildingId(1), "Test Building", this.standardOpeningHours, new Address("1234AB", "Kerelman", 13, "")));
    }

    @Test
    void shouldThrowIfWorkplaceAlreadyExists() {
        this.building.registerWorkplace(new Workplace(new WorkplaceId(1L), 1, 1));

        assertThrows(RuntimeException.class, () -> this.building.registerWorkplace(new Workplace(new WorkplaceId(1L), 1, 1)));
    }

    @Test
    void shouldReturnFalseIfWorkplaceIsNotInBuilding() {
        assertFalse(this.building.includesWorkplace(new WorkplaceId(1L)));
    }

    @Test
    void shouldReturnTrueIfWorkplaceIsInBuilding() {
        this.building.registerWorkplace(new Workplace(new WorkplaceId(1L), 1, 1));

        assertTrue(this.building.includesWorkplace(new WorkplaceId(1L)));
    }

    @Test
    void shouldReturnTrueIfTimeIsOutsideOfOpeningHours() {
        assertTrue(this.building.isTimeOutsideOfOpeningHoursForGivenDay(LocalTime.of(0, 0), LocalTime.of(1, 0), DayOfWeek.MONDAY));
    }

    @Test
    void shouldReturnFalseIfTimeIsInsideOpeningHours() {
        assertFalse(this.building.isTimeOutsideOfOpeningHoursForGivenDay(LocalTime.of(9, 0), LocalTime.of(10, 0), DayOfWeek.MONDAY));
    }
}
