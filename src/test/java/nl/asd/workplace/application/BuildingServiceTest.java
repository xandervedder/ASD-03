package nl.asd.workplace.application;

import nl.asd.reservation.application.ReservationService;
import nl.asd.reservation.domain.ReservationRepository;
import nl.asd.reservation.port.adapter.FakeReservationRepository;
import nl.asd.shared.id.BuildingId;
import nl.asd.shared.id.WorkplaceId;
import nl.asd.workplace.domain.*;
import nl.asd.workplace.port.adapter.FakeBuildingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BuildingServiceTest {
    private BuildingRepository buildingRepository;

    private BuildingService buildingService;
    private ReservationService reservationService;
    private ReservationRepository reservationRepository;

    private WorkplaceId workplaceId;
    private LocalDate reservationDate;
    private LocalTime from;
    private LocalTime to;

    private HashMap<DayOfWeek, OpeningTime> openingHours;

    private int dayInSeconds = 86400;


    private WorkplaceId workplaceId1;
    private Workplace workplace;

    @BeforeEach
    public void initialize() {
        this.workplaceId = new WorkplaceId(1L);
        this.workplaceId1 = new WorkplaceId(2L);
        this.reservationDate = LocalDate.now().plusDays(1);
        this.from = LocalTime.of(12, 30);
        this.to = LocalTime.of(13, 0);

        this.reservationRepository = new FakeReservationRepository();
        this.reservationService = new ReservationService(this.reservationRepository, new BuildingService(buildingRepository));
        this.buildingRepository = new FakeBuildingRepository();
        this.buildingService = new BuildingService(this.buildingRepository);
        this.workplace = new Workplace(workplaceId1, 0, 0);

        openingHours = new HashMap<>();
        openingHours.put(DayOfWeek.MONDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        openingHours.put(DayOfWeek.TUESDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        openingHours.put(DayOfWeek.WEDNESDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        openingHours.put(DayOfWeek.THURSDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        openingHours.put(DayOfWeek.FRIDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        openingHours.put(DayOfWeek.SATURDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
        openingHours.put(DayOfWeek.SUNDAY, new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)));
    }

    @Test
    public void doesWorkplaceExistShouldReturnFalseIfWorkpalceDoesNotExist() {
        assertFalse(buildingService.doesWorkplaceExist(workplaceId));
    }

    @Test
    public void doesWorkplaceExistShouldReturnTrueIfWorkpalceDoesExist() {
        Building building = buildingService.addBuilding("testBuilding");
        buildingService.addWorkplace(building.getId(), workplace.getNumber(), workplace.getFloor());
        buildingService.addWorkplacesToBuilding(building.getId(), List.of(workplace));
        assertTrue(buildingService.doesWorkplaceExist(building.getWorkplaces().get(0).getId()));
    }

    @Test
    public void createBuildingShouldReturnBuilding() {
        assertEquals(Building.class, buildingService.addBuilding("testBuilding").getClass());
    }

    @Test
    public void createBuildingShouldReturnValidBuildingId() {
        assertEquals(new BuildingId(1), buildingService.addBuilding("testBuilding").getId());
        assertEquals(new BuildingId(2), buildingService.addBuilding("testBuilding1").getId());
        assertEquals(new BuildingId(3), buildingService.addBuilding("testBuilding2").getId());
    }

    @Test
    public void createBuildingShouldMakeDefaultWorkdayHashMapWhenOnlyANameIsSupplied() {
        assertEquals(openingHours, buildingService.addBuilding("testBuilding").getOpeningHours());
    }

    @Test
    public void createBuildingShouldHaveEmptyWorkplaceArray() {
        assertEquals(0, buildingService.addBuilding("testBuilding").getWorkplaces().size());
    }

    @Test
    public void addWorkplacesToBuildingShouldAddWorkplacesToBuilding() {
        Building building = buildingService.addBuilding("testBuilding");
        building.addWorkplaces(List.of(new Workplace(workplaceId, 1, 1)));
        assertEquals(1, building.getWorkplaces().size());
    }

    @Test
    public void AddWorkplacesToBuildingViaGetterShouldNotAddWorkplaces() {
        Building building = buildingService.addBuilding("testBuilding");
        assertThrows(UnsupportedOperationException.class,
                () -> building.getWorkplaces().add(new Workplace(workplaceId, 1, 1)));
    }

    @Test
    public void addWorkplacesToBuildingShouldAdd() {
        Building building = buildingService.addBuilding("testBuilding");
        buildingService.addWorkplacesToBuilding(building.getId(), List.of(workplace));
        assertEquals(1, building.getWorkplaces().size());
    }
}
