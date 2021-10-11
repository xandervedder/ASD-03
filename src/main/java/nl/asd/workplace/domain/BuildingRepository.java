package nl.asd.workplace.domain;

import nl.asd.shared.id.BuildingId;
import nl.asd.shared.id.WorkplaceId;

import java.util.List;

public interface BuildingRepository {
    Building ofId(BuildingId id);

    Building findByWorkplace(WorkplaceId id);

    BuildingId nextId();

    List<Building> findAll();

    void save(Building building);
}
