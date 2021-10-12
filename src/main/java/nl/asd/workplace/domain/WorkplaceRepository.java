package nl.asd.workplace.domain;

import nl.asd.shared.id.WorkplaceId;

import java.util.List;

public interface WorkplaceRepository {
    Workplace ofId(WorkplaceId id);

    List<Workplace> findAll();

    void save(Workplace workplace);

    void delete(WorkplaceId id);

    WorkplaceId nextId();
}
