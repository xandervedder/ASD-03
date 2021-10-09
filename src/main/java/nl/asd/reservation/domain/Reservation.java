package nl.asd.reservation.domain;

import nl.asd.shared.id.WorkplaceId;

import java.time.LocalDate;
import java.time.LocalTime;
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

    public void reserveTimeslot(LocalTime from, LocalTime to, ReservationRepository repository) {
        var timeslot = new Timeslot(from, to);
        // Check if we have the same slot already added to this reservation
        for (var slot : this.slots) {
            if (slot.equals(timeslot)) {
                // Custom exception would be better here..
                throw new RuntimeException("This timeslot has already been added");
            }
        }

        // We also would like to see
        var reservations = repository.findByWorkplace(this.workplace);
        for (var reservation : reservations) {
            // Check if other timeslots conflict with the given one
            var timeslots = reservation.slots;
            // TODO: compare by day
            if (timeslots.stream().anyMatch(t -> t.conflictsWith(timeslot))) {
                throw new RuntimeException("This has already been reserved");
            }
        }

        this.slots.add(timeslot);
    }

    public void reserveTimeslots(List<LocalTime> fromList, List<LocalTime> toList, ReservationRepository repository) {
        // This should eventually be done with some type of object..
        if (fromList.size() != toList.size()) {
            throw new IllegalArgumentException("Time range lists should be of same size");
        }

        for (int i = 0; i < fromList.size(); i++) {
            this.reserveTimeslot(fromList.get(i), toList.get(i), repository);
        }
    }

    //checks if the day of cancellation is not the same day as the reservation
    public boolean isCancellationAllowed(LocalDate cancelDate) {
        return cancelDate.getYear() == this.reservationDate.getYear() &&
                cancelDate.getDayOfYear() < this.reservationDate.getDayOfYear();
    }
}
