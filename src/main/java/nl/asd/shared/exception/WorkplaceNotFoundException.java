package nl.asd.shared.exception;

public class WorkplaceNotFoundException extends RuntimeException{
    public WorkplaceNotFoundException(String message) {
        super(message);
    }
}
