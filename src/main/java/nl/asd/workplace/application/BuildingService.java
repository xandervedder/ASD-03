package nl.asd.workplace.application;

import nl.asd.shared.id.WorkplaceId;
import nl.asd.workplace.domain.BuildingRepository;

import java.time.LocalDate;
import java.time.LocalTime;

public class BuildingService {
    private final BuildingRepository repository;

    public BuildingService(BuildingRepository repository) {
        this.repository = repository;
    }

    public boolean isTimeOutsideOfOpeningHoursForGivenDay(WorkplaceId id, LocalDate date, LocalTime from, LocalTime to) {
        var building = repository.findByWorkplace(id);
        return building.isTimeOutsideOfOpeningHoursForGivenDay(from, to, date.getDayOfWeek());
    }
}
