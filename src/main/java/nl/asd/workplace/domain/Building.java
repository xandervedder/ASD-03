package nl.asd.workplace.domain;

import nl.asd.shared.id.BuildingId;
import nl.asd.shared.id.WorkplaceId;

import java.text.MessageFormat;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Building {
    private BuildingId id;
    private String name;
    private Address address;
    private HashMap<DayOfWeek, OpeningTime> openingHours;
    private final List<Workplace> workplaces;


    //TODO: overload this to second constructor
    public Building(BuildingId id, String name, Address address) {
        this(id, name, standardOpeningHours(), address);
    }

    public Building(BuildingId id, String name, HashMap<DayOfWeek, OpeningTime> openingHours, Address address) {
        this.id = id;
        this.setName(name);
        this.setAddress(address);
        setOpeningHours(openingHours);
        this.workplaces = new ArrayList<>();
    }

    /**
     * @return HashMap with days: Mo-Fr | OpeningHours: 08:00-18:00
     */
    private static HashMap<DayOfWeek, OpeningTime> standardOpeningHours() {
        return Stream.of(getDayOfWeeks()).collect(Collectors.toMap(dayOfWeek -> dayOfWeek, dayOfWeek -> new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)), (a, b) -> b, HashMap::new));
    }

    private static DayOfWeek[] getDayOfWeeks() {
        return new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY};
    }

    public boolean includesWorkplace(WorkplaceId id) {
        return this.workplaces.stream().anyMatch(w -> w.getId().equals(id));
    }

    public void registerWorkplace(Workplace workplace) {
        if (this.workplaces.contains(workplace)) {
            throw new RuntimeException("This building already contains this workplace");
        }

        for (Workplace workplace1 : this.workplaces) {
            if (workplace1.getId().equals(workplace.getId())) {
                throw new RuntimeException(MessageFormat.format("Workplace with id: {0} already exists", workplace1.getId()));
            }
        }

        this.workplaces.add(workplace);
    }

    public void registerWorkplaces(List<Workplace> workplaces) {
        for (Workplace workplace : workplaces) {
            registerWorkplace(workplace);
        }
    }

    public boolean isTimeOutsideOfOpeningHoursForGivenDay(LocalTime from, LocalTime to, DayOfWeek day) {
        var openingTimeForDay = this.openingHours.get(day);
        return !(openingTimeForDay.from().isBefore(to) && from.isBefore(openingTimeForDay.to()));
    }


    public void setId(BuildingId id) {
        this.id = id;
    }


    public void setName(String name) {
        if (name.length() < 2) {
            throw new RuntimeException("Name of a building should be longer than 2");
        }

        for (char c : name.toCharArray()) {
            if (!Character.isAlphabetic(c) && !Character.isDigit(c) && !Character.isSpaceChar(c)) {
                throw new RuntimeException("Name contains invalid characters (no digits or alphabetical characters)");
            }
        }

        this.name = name;
    }

    public void setOpeningHours(HashMap<DayOfWeek, OpeningTime> openingHours) {
        if (openingHours == null) {
            throw new RuntimeException("Openinghours cannot be null");
        }

        if (openingHours.entrySet().size() == 0) {
            throw new RuntimeException("Openinghours cannot be null");
        }

        this.openingHours = openingHours;
    }

    public List<Workplace> getWorkplaces() {
        return Collections.unmodifiableList(workplaces);
    }


    public void setAddress(Address address) {
        this.address = address;
    }

    public BuildingId getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public Address getAddress() {
        return address;
    }

    public HashMap<DayOfWeek, OpeningTime> getOpeningHours() {
        return openingHours;
    }
}
