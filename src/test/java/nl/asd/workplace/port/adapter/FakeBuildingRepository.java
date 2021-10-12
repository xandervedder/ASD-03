package nl.asd.workplace.port.adapter;

import nl.asd.shared.id.BuildingId;
import nl.asd.shared.id.WorkplaceId;
import nl.asd.workplace.domain.Building;
import nl.asd.workplace.domain.BuildingRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FakeBuildingRepository implements BuildingRepository {
    private final HashMap<BuildingId, Building> store = new HashMap<>();
    private long internalIncrementor = 0L;

    @Override
    public Building ofBuildingId(BuildingId id) {
        return this.store.get(id);
    }

    @Override
    public Building findByWorkplace(WorkplaceId id) {
        return this.store.values().stream()
                .filter(b -> b.includesWorkplace(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public BuildingId nextBuildingId() {
        this.internalIncrementor++;
        return new BuildingId(this.internalIncrementor);
    }

    @Override
    public List<Building> findAllBuildings() {
        return new ArrayList<>(this.store.values());
    }

    @Override
    public void saveBuilding(Building building) {
        this.store.put(building.getId(), building);
    }

    @Override
    public WorkplaceId nextWorkplaceId() {
        this.internalIncrementor++;
        return new WorkplaceId(this.internalIncrementor);
    }
}
