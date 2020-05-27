package jvn.Cars.exceptionHandler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class InvalidGearBoxTypeDataException extends RuntimeException {
    private HttpStatus httpStatus;

    public InvalidGearBoxTypeDataException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}

