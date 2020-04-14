package jvn.RentACar.exceptionHandler;

public class InvalidFuelTypeDataException extends RuntimeException {

    public InvalidFuelTypeDataException(String message) {
        super(message);
    }
}
