package nl.asd.workplace.application;

import nl.asd.shared.id.WorkplaceId;
import nl.asd.workplace.domain.BuildingRepository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class BuildingService {
    private final BuildingRepository repository;

    public BuildingService(BuildingRepository repository) {
        this.repository = repository;
    }

    public boolean isTimeOutsideOfOpeningHoursForGivenDay(WorkplaceId id, LocalDateTime from, LocalDateTime to) {
        var building = repository.findByWorkplace(id);
        return building.isTimeOutsideOfOpeningHoursForGivenDay(from.toLocalTime(), to.toLocalTime(), from.getDayOfWeek());
    }
}
