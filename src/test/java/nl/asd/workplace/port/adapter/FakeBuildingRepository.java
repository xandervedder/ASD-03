package nl.asd.workplace.port.adapter;

import nl.asd.shared.id.BuildingId;
import nl.asd.shared.id.WorkplaceId;
import nl.asd.workplace.domain.Building;
import nl.asd.workplace.domain.BuildingRepository;
import nl.asd.workplace.domain.Workplace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class FakeBuildingRepository implements BuildingRepository {
    private final HashMap<BuildingId, Building> buildingStore = new HashMap<>();
    private long internalIncrementer = 0L;

    @Override
    public Building ofBuildingId(BuildingId id) {
        return this.buildingStore.get(id);
    }

    @Override
    public Building findByWorkplace(WorkplaceId id) {
        return this.buildingStore.values().stream()
                .filter(b -> b.includesWorkplace(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public BuildingId nextBuildingId() {
        this.internalIncrementer++;
        return new BuildingId(this.internalIncrementer);
    }

    @Override
    public List<Building> findAllBuildings() {
        return new ArrayList<>(this.buildingStore.values());
    }

    @Override
    public void saveBuilding(Building building) {
        this.buildingStore.put(building.getId(), building);
    }

    @Override
    public List<Workplace> findAllWorkplacesForBuilding(BuildingId id) {
        return buildingStore.values().stream().flatMap(building -> building.getWorkplaces().stream()).collect(Collectors.toList());
    }

    @Override
    public void saveWorkplace(BuildingId id, Workplace workplace) {
        buildingStore.get(id).addWorkplaces(List.of(workplace));
    }

    @Override
    public WorkplaceId nextWorkplaceId() {
        this.internalIncrementer++;
        return new WorkplaceId(this.internalIncrementer);
    }
}
