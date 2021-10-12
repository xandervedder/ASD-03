package nl.asd.workplace.port.adapter;

import nl.asd.shared.id.BuildingId;
import nl.asd.shared.id.WorkplaceId;
import nl.asd.workplace.domain.Workplace;
import nl.asd.workplace.domain.WorkplaceRepository;

import java.util.HashMap;
import java.util.List;

public class FakeWorkplaceRepository implements WorkplaceRepository {
    private final HashMap<WorkplaceId, Workplace> store = new HashMap<>();
    private long internalIncrementer = 0L;

    @Override
    public Workplace ofId(WorkplaceId id) {
        return this.store.get(id);
    }

    @Override
    public List<Workplace> findAll() {
        return this.store.values().stream().toList();
    }

    @Override
    public void save(Workplace workplace) {
        this.store.put(workplace.getId(), workplace);
    }

    @Override
    public void delete(WorkplaceId id) {
        this.store.remove(id);
    }

    @Override
    public WorkplaceId nextId() {
        this.internalIncrementer++;
        return new WorkplaceId(this.internalIncrementer);
    }
}
