package nl.asd.reservation.domain;

import nl.asd.shared.id.WorkplaceId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
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
            throw new IllegalArgumentException("Reseration date cannot be in the past");
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

    public boolean conflictsWith(Reservation other) {
        for(var slot : this.slots) {
            for(var otherSlot : other.slots) {
                if(slot.conflictsWith(otherSlot)) {
                    return true;
                }
            }
        }
        return false;
    }

    // DONE: check if new workplace is different from original workplace.
    // TODO Jort: check if new workplace is not in use on selected timeslots.
    // TODO Jort: swap new workplace in.
    public void changeWorkplace(WorkplaceId newWorkplaceId, ReservationRepository repository) {
        if(this.workplace.equals(newWorkplaceId)) {
            throw new RuntimeException("This reservation already uses this workplace");
        }

        for (var reservation : repository.findByWorkplace(newWorkplaceId)) {
            if(this.conflictsWith(reservation)) {
                throw new RuntimeException("This reservation can't occupy a used timeslot");
            }
        }

        this.workplace = newWorkplaceId;
    }

    //checks if the day of cancellation is not the same day as the reservation
    public boolean isCancellationAllowed(LocalDate cancelDate) {
        return cancelDate.getYear() == this.reservationDate.getYear() &&
                cancelDate.getDayOfYear() < this.reservationDate.getDayOfYear();
    }
}
