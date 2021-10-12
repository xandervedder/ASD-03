package nl.asd.workplace.application;

import nl.asd.shared.id.WorkplaceId;
import nl.asd.workplace.domain.Address;
import nl.asd.workplace.domain.Building;
import nl.asd.workplace.domain.BuildingRepository;
import nl.asd.workplace.domain.OpeningTime;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

public class BuildingService {
    private final BuildingRepository repository;

    public BuildingService(BuildingRepository buildingRepository) {
        this.repository = buildingRepository;
    }

    public Building registerBuilding(String name, Address address) {
       return this.registerBuilding(name, Building.standardOpeningHours(), address);
    }

    public Building registerBuilding(String name, HashMap<DayOfWeek, OpeningTime> openingHours, Address address) {
        Building building = new Building(this.repository.nextBuildingId(), name, openingHours, address);
        this.repository.save(building);
        return building;
    }

    public boolean doesWorkplaceExist(WorkplaceId workplaceId) {
        return this.repository.findByWorkplace(workplaceId) != null;
    }

    public boolean isTimeOutsideOfOpeningHoursForGivenDay(WorkplaceId id, LocalDate date, LocalTime from, LocalTime to) {
        var building = this.repository.findByWorkplace(id);
        return building.isTimeOutsideOfOpeningHoursForGivenDay(from, to, date.getDayOfWeek());
    }
}
