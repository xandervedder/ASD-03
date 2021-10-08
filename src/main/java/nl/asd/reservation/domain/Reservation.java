package nl.asd.reservation.domain;

import nl.asd.shared.id.WorkplaceId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Reservation {
    private ReservationId id;
    private LocalDate createdAt;
    private LocalDate date;
    private ReservationType type;
    private List<Timeslot> slots;

    private WorkplaceId workplace;

    public Reservation(ReservationId id, LocalDate createdAt, LocalDate date, ReservationType type, WorkplaceId workplace) {
        // w.i.p. validatie
        this.id = id;
        this.createdAt = createdAt;
        this.date = date;
        this.type = type;
        this.slots = new ArrayList<>();

        this.workplace = workplace;
    }

    public ReservationId getId() {
        return id;
    }

    public void setId(ReservationId id) {
        this.id = id;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getDate() { return date; }

    public void setDate(LocalDate date) { this.date = date; }

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
        var reservations = repository.findByWorkplace(this.workplace);
        for (var reservation : reservations) {
            // Bepaal of de gewenste timeslot in de andere timeslots zit (conflicteert met)
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

    //checks if the day of cancellation is not the same day as the reservation
    public boolean isCancellationAllowed(LocalDate cancelDate) {
        return cancelDate.getYear() == this.date.getYear() &&
                cancelDate.getDayOfYear() < this.date.getDayOfYear();
    }
}
