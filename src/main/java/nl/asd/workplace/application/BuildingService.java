package nl.asd.workplace.application;

import nl.asd.shared.id.BuildingId;
import nl.asd.shared.id.WorkplaceId;
import nl.asd.workplace.domain.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

public class BuildingService {
    private final BuildingRepository repository;

    public BuildingService(BuildingRepository buildingRepository) {
        this.repository = buildingRepository;
    }

    public Building registerBuilding(String name, Address address) {
        Building building = new Building(repository.nextBuildingId(), name, address);
        // In reality this would be bad, but this would be solved by using Spring which returns the instance on save
        repository.saveBuilding(building);
        return repository.ofBuildingId(building.getId());
    }

    public BuildingId registerBuilding(String name, HashMap<DayOfWeek, OpeningTime> openingHours, Address address) {
        Building building = new Building(repository.nextBuildingId(), name, openingHours, address);
        repository.saveBuilding(building);
        return repository.ofBuildingId(building.getId()).getId();
    }

    // ---------------------------------------------------------------------------------------------------------- //

    public boolean doesWorkplaceExist(WorkplaceId workplaceId) {
        return this.repository.findByWorkplace(workplaceId) != null;
    }

    public boolean isTimeOutsideOfOpeningHoursForGivenDay(WorkplaceId id, LocalDate date, LocalTime from, LocalTime to) {
        var building = repository.findByWorkplace(id);
        return building.isTimeOutsideOfOpeningHoursForGivenDay(from, to, date.getDayOfWeek());
    }

    // ---------------------------------------------------------------------------------------------------------- //

    // Workplace always needs a building
    public WorkplaceId registerWorkplaceToBuilding(BuildingId id, int number, int floor) {
        Workplace workplace = new Workplace(repository.nextWorkplaceId(), number, floor);
        Building building = repository.ofBuildingId(id);
        building.registerWorkplace(workplace);
        this.repository.saveBuilding(building);
        return workplace.getId();
    }

    public Building addWorkplacesToBuilding(BuildingId id, List<Workplace> workplaces) {
        var building = repository.ofBuildingId(id);
        building.registerWorkplaces(workplaces);
        repository.saveBuilding(building);
        return repository.ofBuildingId(building.getId());
    }
}
