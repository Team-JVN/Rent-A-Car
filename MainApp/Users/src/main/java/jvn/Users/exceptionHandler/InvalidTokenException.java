package jvn.Users.exceptionHandler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class InvalidTokenException extends RuntimeException {

    private HttpStatus httpStatus;

    public InvalidTokenException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
