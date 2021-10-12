package nl.asd.workplace.application;

import nl.asd.shared.id.BuildingId;
import nl.asd.shared.id.WorkplaceId;
import nl.asd.workplace.domain.Building;
import nl.asd.workplace.domain.BuildingRepository;
import nl.asd.workplace.domain.Workplace;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class BuildingService {
    private final BuildingRepository repository;

    public BuildingService(BuildingRepository buildingRepository) {
        this.repository = buildingRepository;
    }

    public boolean isTimeOutsideOfOpeningHoursForGivenDay(WorkplaceId id, LocalDate date, LocalTime from, LocalTime to) {
        var building = repository.findByWorkplace(id);
        return building.isTimeOutsideOfOpeningHoursForGivenDay(from, to, date.getDayOfWeek());
    }

    // Rename to doesBuildingContainWorkplace and then make a separate method for doesWorkplaceExist?
    public boolean doesWorkplaceExist(WorkplaceId workplaceId) {
        return this.repository.findByWorkplace(workplaceId) != null;
    }

    // Workplace always needs a building
    public WorkplaceId addWorkplace(BuildingId id, int number, int floor) {
        Workplace workplace = new Workplace(repository.nextWorkplaceId(), number, floor);
        Building building = repository.ofBuildingId(id);
        building.addWorkplace(workplace);
        this.repository.saveBuilding(building);
        return workplace.getId();
    }

    public Building addBuilding(String name) {
        Building building = new Building(repository.nextBuildingId(), name);
        // In reality this would be bad, but this would be solved by using Spring which returns the instance on save
        repository.saveBuilding(building);
        return repository.ofBuildingId(building.getId());
    }

    public Building addWorkplacesToBuilding(BuildingId id, List<Workplace> workplaces) {
        var building = repository.ofBuildingId(id);
        building.addWorkplaces(workplaces);
        repository.saveBuilding(building);
        return repository.ofBuildingId(building.getId());
    }
}
