package jvn.RentACar.exceptionHandler;

import org.springframework.http.HttpStatus;

public class InvalidPriceListDataException extends RuntimeException {

    private HttpStatus httpStatus;

    public InvalidPriceListDataException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
