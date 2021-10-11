package nl.asd.workplace.domain;

import nl.asd.shared.id.BuildingId;
import nl.asd.shared.id.WorkplaceId;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Building {
    private BuildingId id;
    private String name;
    // TODO: Don't think HashMap is the right choice here..., maybe a HashSet or some other Value object?
    private HashMap<DayOfWeek, OpeningTime> openingHours;
    private List<Workplace> workplaces;

    public Building(BuildingId id, String name) {
        this.id = id;
        this.name = name;
        this.openingHours = standardOpeningHours();
        this.workplaces = new ArrayList<>();
    }

    public Building(BuildingId id, String name, HashMap<DayOfWeek, OpeningTime> openingHours) {
        this.id = id;
        this.name = name;
        this.openingHours = openingHours;
        this.workplaces = new ArrayList<>();
    }

    /**
     * @return HashMap with days: Mo-Fr | OpeningHours: 08:00-18:00
     */
    private HashMap<DayOfWeek, OpeningTime> standardOpeningHours() {
        return Stream.of(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
                .collect(Collectors.toMap(
                        dayOfWeek ->
                                dayOfWeek, dayOfWeek ->
                                new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0)), (a, b) -> b, HashMap::new));
    }

    public boolean includesWorkplace(WorkplaceId id) {
        return this.workplaces.stream().anyMatch(w -> w.getId().equals(id));
    }

    public void addWorkplaces(List<Workplace> workplaces) {
        for (Workplace workplace : workplaces)
            if (!this.workplaces.contains(workplace))
                this.workplaces.add(workplace);
    }

    public void registerWorkplace(Workplace workplace) {
        // TODO: add more business rules @JustMilan
        if (this.workplaces.contains(workplace)) {
            throw new RuntimeException("This building already contains this workplace");
        }

        this.workplaces.add(workplace);
    }

    public boolean isTimeOutsideOfOpeningHoursForGivenDay(LocalTime from, LocalTime to, DayOfWeek day) {
        var openingTimeForDay = this.openingHours.get(day);
        return !(openingTimeForDay.from().isBefore(to) && from.isBefore(openingTimeForDay.to()));
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

    public HashMap<DayOfWeek, OpeningTime> getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(HashMap<DayOfWeek, OpeningTime> openingHours) {
        this.openingHours = openingHours;
    }
}
