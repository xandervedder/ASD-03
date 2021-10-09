package nl.asd.reservation.domain;

import nl.asd.shared.id.WorkplaceId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Reservation {
    private final LocalDate createdAt;
    private final List<Timeslot> slots;

    private ReservationId id;
    private LocalDate reservationDate;
    private ReservationType type;
    private WorkplaceId workplace;

    public Reservation(ReservationId id, LocalDate reservationDate, ReservationType type, WorkplaceId workplace) {
        this.createdAt = LocalDate.now();
        this.slots = new ArrayList<>();

        this.setId(id);
        this.setReservationDate(reservationDate);
        this.setType(type);
        this.setWorkplace(workplace);
    }

    public ReservationId getId() {
        return id;
    }

    public void setId(ReservationId id) {
        if (id == null) {
            throw new IllegalArgumentException("Cannot create reservation without an id");
        }

        this.id = id;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        if (reservationDate == null) {
            throw new IllegalArgumentException("Cannot create a reservation without a reservation date");
        }

        if (reservationDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot create a reservation that is in the past");
        }

        this.reservationDate = reservationDate;
    }

    public ReservationType getType() {
        return type;
    }

    public void setType(ReservationType type) {
        if (type == null) {
            throw new IllegalArgumentException("Cannot create a reservation without a reservation type");
        }

        this.type = type;
    }

    public List<Timeslot> getSlots() {
        return slots;
    }

    public WorkplaceId getWorkplace() {
        return workplace;
    }

    public void setWorkplace(WorkplaceId workplace) {
        if (workplace == null) {
            throw new IllegalArgumentException("Cannot create a reservation without an associated workplace");
        }

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
            // TODO: compare by day
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
        return cancelDate.getYear() == this.reservationDate.getYear() &&
                cancelDate.getDayOfYear() < this.reservationDate.getDayOfYear();
    }
}
