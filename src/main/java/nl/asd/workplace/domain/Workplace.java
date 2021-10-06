package nl.asd.workplace.domain;

import nl.asd.reservation.domain.ReservationId;

import java.util.List;

public class Workplace {
    private long id;
    private int number;
    private int floor;
    private List<ReservationId> reservations;

    public Workplace(long id, int number, int floor) {
        this.id = id;
        this.number = number;
        this.floor = floor;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public void reserve(ReservationId reservation) {

    }

    public boolean isReserved() {
        return false;
    }
}
