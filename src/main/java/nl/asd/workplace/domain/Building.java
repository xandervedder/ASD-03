package nl.asd.workplace.domain;

public class Building {
    private long id;
    private String name;

    public Building(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
