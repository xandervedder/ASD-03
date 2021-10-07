package nl.asd.workplace.domain;

import nl.asd.shared.id.BuildingId;

import java.util.List;

public interface BuildingRepository {
    Building ofId(BuildingId id);
    BuildingId nextId();
    List<Building> findAll();
    void save(Building building);
}
