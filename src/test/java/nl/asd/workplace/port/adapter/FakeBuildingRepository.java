package nl.asd.workplace.port.adapter;

import nl.asd.reservation.domain.ReservationId;
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
    public Building ofId(BuildingId id) {
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
    public BuildingId nextId() {
        this.internalIncrementor++;
        return new BuildingId(this.internalIncrementor);
    }

    @Override
    public List<Building> findAll() {
        return new ArrayList<>(this.store.values());
    }

    @Override
    public void save(Building building) {
        this.store.put(building.getId(), building);
    }
}
