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
        return new Building(repository.nextId(), name);
    }

    public Building addWorkplacesToBuilding(BuildingId id, List<Workplace> workplaces) {
        var building = repository.ofId(id);
        building.addWorkplaces(workplaces);
        return building;
    }
}
