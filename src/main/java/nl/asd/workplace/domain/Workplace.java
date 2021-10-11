package nl.asd.workplace.domain;

import nl.asd.shared.id.WorkplaceId;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Workplace workplace = (Workplace) o;
        return number == workplace.number && floor == workplace.floor && Objects.equals(id, workplace.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number, floor);
    }
}


