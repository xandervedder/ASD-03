package nl.asd.workplace.application;

import nl.asd.reservation.domain.ReservationRepository;
import nl.asd.reservation.port.adapter.FakeReservationRepository;
import nl.asd.shared.id.BuildingId;
import nl.asd.shared.id.WorkplaceId;
import nl.asd.workplace.domain.*;
import nl.asd.workplace.port.adapter.FakeBuildingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BuildingServiceTest {
    private BuildingRepository buildingRepository;
    private BuildingService buildingService;
    private ReservationRepository reservationRepository;

    private WorkplaceId workplaceId;
    private HashMap<DayOfWeek, OpeningTime> openingHours;
    private int dayInSeconds = 86400;

    private WorkplaceId workplaceId1;
    private Workplace workplace;
    private Address address;
    private String testName;

    @BeforeEach
    public void initialize() {
        this.workplaceId = new WorkplaceId(1L);
        this.workplaceId1 = new WorkplaceId(2L);
        this.address = new Address("1234AB", "Kerelman", 13, "");
        this.testName = "TestBuilding";

        this.reservationRepository = new FakeReservationRepository();
        this.buildingRepository = new FakeBuildingRepository();
        this.buildingService = new BuildingService(this.buildingRepository);
        this.workplace = new Workplace(workplaceId1, 0, 0);

        openingHours = new HashMap<>();
        openingHours.put(DayOfWeek.MONDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        openingHours.put(DayOfWeek.TUESDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        openingHours.put(DayOfWeek.WEDNESDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        openingHours.put(DayOfWeek.THURSDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        openingHours.put(DayOfWeek.FRIDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
    }

    @Test
    public void registerBuildingShouldReturnBuilding() {
        assertEquals(Building.class, buildingService.registerBuilding(testName, address).getClass());
    }

    @Test
    public void registerBuildingShouldReturnValidBuildingId() {
        assertEquals(new BuildingId(1), buildingService.registerBuilding(testName, address).getId());
        assertEquals(new BuildingId(2), buildingService.registerBuilding("testBuilding1", new Address("4321BA", "Karelvrouw", 46, "")).getId());
        assertEquals(new BuildingId(3), buildingService.registerBuilding("testBuilding2", new Address("6547DG", "GielKerel", 83, "MAN")).getId());
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
        Building building = buildingService.registerBuilding(testName, address);
        building.registerWorkplaces(List.of(new Workplace(workplaceId, 1, 1)));
        assertEquals(1, building.getWorkplaces().size());
    }

    @Test
    public void registerWorkplacesToBuildingViaGetterShouldNotAddWorkplaces() {
        Building building = buildingService.registerBuilding(testName, address);
        assertThrows(UnsupportedOperationException.class,
                () -> building.getWorkplaces().add(new Workplace(workplaceId, 1, 1)));
    }

    @Test
    public void registerWorkplacesToBuildingShouldAdd() {
        Building building = buildingService.registerBuilding(testName, address);
        buildingService.addWorkplacesToBuilding(building.getId(), List.of(workplace));
        assertEquals(1, building.getWorkplaces().size());
    }
}
