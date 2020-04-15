package jvn.RentACar.exceptionHandler;

public class InvalidCarDataException extends RuntimeException {

    public InvalidCarDataException(String message) {
        super(message);
    }
}