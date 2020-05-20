package jvn.RentACar.exceptionHandler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class InvalidPriceListDataException extends RuntimeException {

    private HttpStatus httpStatus;

    public InvalidPriceListDataException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
