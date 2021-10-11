package nl.asd.workplace;

public class WorkplaceNotFoundException extends RuntimeException{
    public WorkplaceNotFoundException(String message) {
        super(message);
    }
}
