package nl.asd.shared.exception;

public class ChangeTimeslotNotAllowedException extends  RuntimeException{
    public ChangeTimeslotNotAllowedException(String message) {
        super(message);
    }
}
