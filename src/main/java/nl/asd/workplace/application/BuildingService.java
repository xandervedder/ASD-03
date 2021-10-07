package nl.asd.workplace.application;

import nl.asd.shared.id.BuildingId;
import nl.asd.workplace.domain.BuildingRepository;

import java.util.List;

public class BuildingService {
    private final BuildingRepository repository;

    public BuildingService(BuildingRepository repository) {
        this.repository = repository;
    }

    public List<String> openingHoursForBuilding(BuildingId id) {
        return null;
    }
}
