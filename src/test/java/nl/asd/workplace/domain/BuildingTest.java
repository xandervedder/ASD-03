package nl.asd.workplace.domain;

import com.sun.source.tree.AssertTree;
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

    BuildingRepository buildingRepository;
    HashMap<DayOfWeek, OpeningTime> standardOpeningHours;
    @BeforeEach
    void initialize() {
        this.buildingRepository = new FakeBuildingRepository();

        this.standardOpeningHours = new HashMap<DayOfWeek, OpeningTime>();
        this.standardOpeningHours.put(DayOfWeek.MONDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        this.standardOpeningHours.put(DayOfWeek.TUESDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        this.standardOpeningHours.put(DayOfWeek.WEDNESDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        this.standardOpeningHours.put(DayOfWeek.THURSDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        this.standardOpeningHours.put(DayOfWeek.FRIDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        this.standardOpeningHours.put(DayOfWeek.SATURDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        this.standardOpeningHours.put(DayOfWeek.SUNDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
    }

    @Test
    void shouldCreateBuildingCorrectly() {
        var building = new Building(this.buildingRepository.nextId(), "Test Building", this.standardOpeningHours);
        this.buildingRepository.save(building);

        assertEquals(1, this.buildingRepository.findAll().size());
    }

    @Test
    void shouldThrowIfWorkplaceAlreadyExists() {
        var building = new Building(this.buildingRepository.nextId(), "Test Building", this.standardOpeningHours);
        building.registerWorkplace(new Workplace(new WorkplaceId(1L), 1, 1));

        assertThrows(RuntimeException.class, () -> building.registerWorkplace(new Workplace(new WorkplaceId(1L), 1, 1)));
    }

    @Test
    void shouldReturnFalseIfWorkplaceIsNotInBuilding() {
        var building = new Building(this.buildingRepository.nextId(), "Test Building", this.standardOpeningHours);
        this.buildingRepository.save(building);

        assertFalse(building.includesWorkplace(new WorkplaceId(1L)));
    }

    @Test
    void shouldReturnTrueIfWorkplaceIsInBuilding() {
        var building = new Building(this.buildingRepository.nextId(), "Test Building", this.standardOpeningHours);
        building.registerWorkplace(new Workplace(new WorkplaceId(1L), 1, 1));
        this.buildingRepository.save(building);

        assertTrue(building.includesWorkplace(new WorkplaceId(1L)));
    }

    @Test
    void shouldReturnTrueIfTimeIsOutsideOfOpeningHours() {
        var building = new Building(this.buildingRepository.nextId(), "Test Building", this.standardOpeningHours);
        assertTrue(building.isTimeOutsideOfOpeningHoursForGivenDay(LocalTime.of(0, 0), LocalTime.of(1, 0), DayOfWeek.MONDAY));
    }

    @Test
    void shouldReturnFalseIfTimeIsInsideOpeningHours() {
        var building = new Building(this.buildingRepository.nextId(), "Test Building", this.standardOpeningHours);
        assertFalse(building.isTimeOutsideOfOpeningHoursForGivenDay(LocalTime.of(9, 0), LocalTime.of(10, 0), DayOfWeek.MONDAY));
    }
}
