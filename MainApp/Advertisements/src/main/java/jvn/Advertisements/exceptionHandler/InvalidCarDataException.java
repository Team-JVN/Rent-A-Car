package jvn.Advertisements.exceptionHandler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class InvalidCarDataException extends RuntimeException {
    private HttpStatus httpStatus;

    public InvalidCarDataException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
