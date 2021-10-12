package nl.asd.workplace.application;

import nl.asd.shared.id.BuildingId;
import nl.asd.shared.id.WorkplaceId;
import nl.asd.workplace.domain.Address;
import nl.asd.workplace.domain.BuildingRepository;
import nl.asd.workplace.domain.OpeningTime;
import nl.asd.workplace.domain.Workplace;
import nl.asd.workplace.port.adapter.FakeBuildingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BuildingServiceTest {
    private BuildingService buildingService;
    private WorkplaceId workplaceId;
    private HashMap<DayOfWeek, OpeningTime> openingHours;
    private Address address;
    private String testName;

    @BeforeEach
    public void initialize() {
        this.workplaceId = new WorkplaceId(1L);
        this.address = new Address("1234AB", "Kerelman", 13, "");
        this.testName = "TestBuilding";

        BuildingRepository buildingRepository = new FakeBuildingRepository();
        this.buildingService = new BuildingService(buildingRepository);

        openingHours = new HashMap<>();
        openingHours.put(DayOfWeek.MONDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        openingHours.put(DayOfWeek.TUESDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        openingHours.put(DayOfWeek.WEDNESDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        openingHours.put(DayOfWeek.THURSDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        openingHours.put(DayOfWeek.FRIDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
    }

    @Test
    public void shouldRegisterBuildingCorrectly() {
        assertDoesNotThrow(() -> this.buildingService.registerBuilding(testName, address));
    }

    @Test
    public void registerBuildingShouldReturnValidBuildingId() {
        assertEquals(new BuildingId(1), this.buildingService.registerBuilding(testName, address).getId());
        assertEquals(new BuildingId(2), this.buildingService.registerBuilding("testBuilding1", new Address("4321BA", "Karelvrouw", 46, "")).getId());
        assertEquals(new BuildingId(3), this.buildingService.registerBuilding("testBuilding2", new Address("6547DG", "GielKerel", 83, "MAN")).getId());
    }

    @Test
    public void registerBuildingShouldMakeDefaultWorkdayHashMapWhenOnlyANameIsSupplied() {
        assertEquals(openingHours, buildingService.registerBuilding(testName, address).getOpeningHours());
    }

    @Test
    public void registerBuildingShouldHaveEmptyWorkplaceArray() {
        assertEquals(0, buildingService.registerBuilding(testName, address).getWorkplaces().size());
    }

    @Test
    public void registerWorkplacesToBuildingShouldAddWorkplacesToBuilding() {
        var building = buildingService.registerBuilding(testName, address);
        building.registerWorkplaces(List.of(new Workplace(workplaceId, 1, 1)));
        assertEquals(1, building.getWorkplaces().size());
    }

    @Test
    public void registerWorkplacesToBuildingViaGetterShouldNotAddWorkplaces() {
        var building = buildingService.registerBuilding(testName, address);
        assertThrows(UnsupportedOperationException.class,
                () -> building.getWorkplaces().add(new Workplace(workplaceId, 1, 1)));
    }
}
