package jvn.RentACar.exceptionHandler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class InvalidRentRequestDataException extends RuntimeException {
    private HttpStatus httpStatus;

    public InvalidRentRequestDataException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}