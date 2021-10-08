package nl.asd.reservation;

public class CancellationNotAllowedException extends  RuntimeException{
    public CancellationNotAllowedException(String message) {
        super(message);
    }
}
