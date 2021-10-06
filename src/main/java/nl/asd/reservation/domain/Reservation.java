package nl.asd.reservation.domain;

import nl.asd.workplace.domain.WorkplaceId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Reservation {
    private long id;
    private LocalDate createdAt;
    private ReservationType type;
    private List<Timeslot> slots;

    private WorkplaceId workplace;

    public Reservation(long id, LocalDate createdAt, ReservationType type, WorkplaceId workplace) {
        // w.i.p. validatie
        this.id = id;
        this.createdAt = createdAt;
        this.type = type;
        this.slots = new ArrayList<>();

        this.workplace = workplace;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public ReservationType getType() {
        return type;
    }

    public void setType(ReservationType type) {
        this.type = type;
    }

    public List<Timeslot> getSlots() {
        return slots;
    }

    public void setSlots(List<Timeslot> slots) {
        this.slots = slots;
    }

    public WorkplaceId getWorkplace() {
        return workplace;
    }

    public void setWorkplace(WorkplaceId workplace) {
        this.workplace = workplace;
    }

    public long totalMinutesReserved() {
        return slots.stream().map(Timeslot::minutes).reduce(0L, Long::sum);
    }

    public void reserveTimeslot(Timeslot timeslot, ReservationRepository repository) {
        // Validatie hier, we willen immers kijken of er een timeslot hierin zit die hetzelfde is
        for (var slot : this.slots) {
            if (slot.equals(timeslot)) {
                // Custom exception is hier ook beter...
                throw new RuntimeException("This timeslot has already been added");
            }
        }

        // Tevens willen we ook zien of een andere reservation op
        // dezelfde workplace al een reservatie heeft zitten...
        var reservations = repository.findAll();
        for (var reservation : reservations) {
            if (!reservation.workplace.equals(this.workplace)) {
                continue;
            }

            // Bepaal de gewenste timeslot in de andere timeslots zit (conflicteert)
            var timeslots = reservation.slots;
            if (timeslots.stream().anyMatch(t -> t.conflictsWith(timeslot))) {
                throw new RuntimeException("This has already been reserved");
            }

        }

        this.slots.add(timeslot);
    }

    public void reserveTimeslots(List<Timeslot> slots, ReservationRepository repository) {
        for (var slot : slots) {
            this.reserveTimeslot(slot, repository);
        }
    }
}
