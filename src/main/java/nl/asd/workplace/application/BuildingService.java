package nl.asd.workplace.application;

import nl.asd.shared.id.BuildingId;
import nl.asd.shared.id.WorkplaceId;
import nl.asd.workplace.domain.Building;
import nl.asd.workplace.domain.BuildingRepository;
import nl.asd.workplace.domain.Workplace;
import nl.asd.workplace.domain.WorkplaceRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class BuildingService {
    private final BuildingRepository buildingRepository;
    // Seperate service for workplace?
    private final WorkplaceRepository workplaceRepository;

    public BuildingService(BuildingRepository buildingRepository, WorkplaceRepository workplaceRepository) {
        this.buildingRepository = buildingRepository;
        this.workplaceRepository = workplaceRepository;
    }

    public boolean isTimeOutsideOfOpeningHoursForGivenDay(WorkplaceId id, LocalDate date, LocalTime from, LocalTime to) {
        var building = buildingRepository.findByWorkplace(id);
        return building.isTimeOutsideOfOpeningHoursForGivenDay(from, to, date.getDayOfWeek());
    }

    // Rename to doesBuildingContainWorkplace and then make a seperate method for doesWorkpalceExist?
    public boolean doesWorkplaceExist(WorkplaceId workplaceId) {
        return this.buildingRepository.findByWorkplace(workplaceId) != null;
    }

    // Avoid skip call
    public boolean createWorkplace(int number, int floor) {
        Workplace workplace = new Workplace(workplaceRepository.nextId(), number, floor);
        this.workplaceRepository.save(workplace);
        return doesWorkplaceExist(workplaceRepository.ofId(workplace.getId()).getId());
    }

    public Building createBuildingWithWorkplaces(String name, List<Workplace> workplaces) {
        Building building = createBuilding(name);
        return addWorkplacesToBuilding(building.getId(), workplaces);
    }

    public Building createBuilding(String name) {
        Building building = new Building(buildingRepository.nextId(), name);
        // In reality this would be bad, but this would be solved by using Spring which returns the instance on save
        buildingRepository.save(building);
        return buildingRepository.ofId(building.getId());
    }

    public Building addWorkplacesToBuilding(BuildingId id, List<Workplace> workplaces) {
        var building = buildingRepository.ofId(id);
        building.addWorkplaces(workplaces);
        buildingRepository.save(building);
        return buildingRepository.ofId(building.getId());
    }
}
