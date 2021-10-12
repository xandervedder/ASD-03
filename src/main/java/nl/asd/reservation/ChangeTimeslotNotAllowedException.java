package nl.asd.reservation;

public class ChangeTimeslotNotAllowedException extends  RuntimeException{
    public ChangeTimeslotNotAllowedException(String message) {
        super(message);
    }
}
