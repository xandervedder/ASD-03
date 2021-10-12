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
    private final BuildingRepository buildingRepository;

    public BuildingService(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    public boolean isTimeOutsideOfOpeningHoursForGivenDay(WorkplaceId id, LocalDate date, LocalTime from, LocalTime to) {
        var building = buildingRepository.findByWorkplace(id);
        return building.isTimeOutsideOfOpeningHoursForGivenDay(from, to, date.getDayOfWeek());
    }

    // Rename to doesBuildingContainWorkplace and then make a seperate method for doesWorkplaceExist?
    public boolean doesWorkplaceExist(WorkplaceId workplaceId) {
        return this.buildingRepository.findByWorkplace(workplaceId) != null;
    }

    // Workplace always needs a building
    public boolean createWorkplace(BuildingId id, int number, int floor) {
        Workplace workplace = new Workplace(buildingRepository.nextWorkplaceId(), number, floor);
        this.buildingRepository.saveWorkplace(id, workplace);
        return doesWorkplaceExist(workplace.getId());
    }

    public Building createBuilding(String name) {
        Building building = new Building(buildingRepository.nextBuildingId(), name);
        // In reality this would be bad, but this would be solved by using Spring which returns the instance on save
        buildingRepository.saveBuilding(building);
        return buildingRepository.ofBuildingId(building.getId());
    }

    public Building addWorkplacesToBuilding(BuildingId id, List<Workplace> workplaces) {
        var building = buildingRepository.ofBuildingId(id);
        building.addWorkplaces(workplaces);
        buildingRepository.saveBuilding(building);
        return buildingRepository.ofBuildingId(building.getId());
    }
}
