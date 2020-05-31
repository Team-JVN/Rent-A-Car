package jvn.Cars.exceptionHandler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class InvalidBodyStyleDataException extends RuntimeException {

    private HttpStatus httpStatus;

    public InvalidBodyStyleDataException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
