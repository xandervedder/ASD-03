package nl.asd.reservation.domain;

import nl.asd.shared.exception.ChangeTimeslotNotAllowedException;
import nl.asd.shared.id.WorkplaceId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Reservation {
    private final LocalDate createdAt;

    private List<Timeslot> slots;
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
            throw new IllegalArgumentException("Reservation id cannot be null");
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
            throw new IllegalArgumentException("Reservation date cannot be null");
        }

        if (reservationDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Reservation date cannot be in the past");
        }

        this.reservationDate = reservationDate;
    }

    public ReservationType getType() {
        return type;
    }

    public void setType(ReservationType type) {
        if (type == null) {
            throw new IllegalArgumentException("Reservation type cannot be null");
        }

        this.type = type;
    }

    public List<Timeslot> getSlots() {
        return Collections.unmodifiableList(this.slots);
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

    public void changeTimeslot(List<Timeslot> newSlots, ReservationRepository repository) {
        // Changing a timeslot is not allowed on the day of the reservation
        if (this.reservationDate.equals(LocalDate.now())) {
            throw new ChangeTimeslotNotAllowedException("Cannot change timeslot on the day of the actual reservation");
        }

        // Changing a timeslot is not allowed of a reservation that is in the past
        if (this.reservationDate.isBefore(LocalDate.now())) {
            throw new ChangeTimeslotNotAllowedException("Cannot change timeslot of a reservation in the past");
        }

        var copy = new ArrayList<>(newSlots);
        this.reserveTimeslots(copy, repository);
        this.slots = copy;
    }

    public void reserveTimeslot(Timeslot timeslot, ReservationRepository repository) {
        // Check if we have the same slot already added to this reservation
        for (var slot : this.slots) {
            if (slot.conflictsWith(timeslot)) {
                // Custom exception would be better here..
                throw new RuntimeException("This timeslot has already been added");
            }
        }

        var reservations = repository.findByWorkplaceAndDate(this.workplace, this.reservationDate);
        for (var reservation : reservations) {
            // Check if other timeslots conflict with the given one
            if (reservation.slots.stream().anyMatch(t -> t.conflictsWith(timeslot))) {
                throw new RuntimeException("This has already been reserved");
            }
        }

        this.slots.add(timeslot);
    }

    public void reserveTimeslots(List<Timeslot> timeslots, ReservationRepository repository) {
        for (var timeslot : timeslots) {
            this.reserveTimeslot(timeslot, repository);
        }
    }

    // Checks if this reservation has any overlapping timeslots reserved on the to be compared to reservation.
    public boolean conflictsWith(Reservation other) {
        for (var slot : this.slots) {
            if (slot.conflictsWith(other.slots)) {
                return true;
            }
        }
        return false;
    }

    // Change the workplace of this reservation.
    public void migrateTo(WorkplaceId newWorkplaceId, ReservationRepository repository) {
        if (newWorkplaceId == null) {
            throw new RuntimeException("New Workplace Id cannot be null");
        }
        // First we check if the current workplace is equal to the new workplace
        if (this.workplace.equals(newWorkplaceId)) {
            // If so, we don't need to change
            throw new RuntimeException("This reservation already uses this workplace");
        }

        // Secondly we need to check if the current reservation conflicts with reserved timeslots on the new workplace.
        for (var reservation : repository.findByWorkplaceAndDate(newWorkplaceId, this.reservationDate)) {
            if (this.conflictsWith(reservation)) {
                throw new RuntimeException("This reservation cannot occupy a used timeslot");
            }
        }

        this.workplace = newWorkplaceId;
    }

    // Checks if the day of cancellation is not the same day as the reservation
    public boolean isCancellationAllowed(LocalDate cancelDate) {
        return cancelDate.getYear() == this.reservationDate.getYear() &&
                cancelDate.getDayOfYear() < this.reservationDate.getDayOfYear();
    }
}
