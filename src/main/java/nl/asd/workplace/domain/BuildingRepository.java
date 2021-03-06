package nl.asd.workplace.domain;

import nl.asd.shared.id.BuildingId;
import nl.asd.shared.id.WorkplaceId;

import java.util.List;

public interface BuildingRepository {
    Building ofBuildingId(BuildingId id);

    Building findByWorkplace(WorkplaceId id);

    BuildingId nextBuildingId();

    List<Building> findAllBuildings();

    void save(Building building);

    WorkplaceId nextWorkplaceId();
}
