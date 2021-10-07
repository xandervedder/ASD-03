package nl.asd.workplace.domain;

import nl.asd.shared.id.BuildingId;

import java.util.HashMap;

public class Building {
    private BuildingId id;
    private String name;
    private HashMap<Day, OpeningHours> openingHours;

    public Building(BuildingId id, String name, HashMap<Day, OpeningHours> openingHours) {
        this.id = id;
        this.name = name;
        this.openingHours = openingHours;
    }

    public BuildingId getId() {
        return id;
    }

    public void setId(BuildingId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<Day, OpeningHours> getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(HashMap<Day, OpeningHours> openingHours) {
        this.openingHours = openingHours;
    }
}
