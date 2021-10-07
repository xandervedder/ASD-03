package nl.asd.workplace.domain;

import nl.asd.shared.id.WorkplaceId;

import java.util.List;

public class Workplace {
    private WorkplaceId id;
    private int number;
    private int floor;

    public Workplace(WorkplaceId id, int number, int floor) {
        this.id = id;
        this.number = number;
        this.floor = floor;
    }

    public WorkplaceId getId() {
        return id;
    }

    public void setId(WorkplaceId id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }
}
