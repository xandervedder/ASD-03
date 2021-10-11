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

    public BuildingService(BuildingRepository repository) {
        this.repository = repository;
    }

    public boolean isTimeOutsideOfOpeningHoursForGivenDay(WorkplaceId id, LocalDate date, LocalTime from, LocalTime to) {
        var building = repository.findByWorkplace(id);
        return building.isTimeOutsideOfOpeningHoursForGivenDay(from, to, date.getDayOfWeek());
    }

    public boolean doesWorkplaceExist(WorkplaceId workplaceId) {
        return this.repository.findByWorkplace(workplaceId) != null;
    }

    public Building createBuilding(String name) {
        Building building = new Building(repository.nextId(), name);
        // In reality this would be bad, but this would be solved by using Spring which returns the instance on save
        repository.save(building);
        return repository.ofId(building.getId());
    }

    public Building addWorkplacesToBuilding(BuildingId id, List<Workplace> workplaces) {
        var building = repository.ofId(id);
        building.addWorkplaces(workplaces);
        repository.save(building);
        return repository.ofId(building.getId());
    }
}
