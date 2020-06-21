package jvn.Renting.exceptionHandler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class InvalidCommentDataException extends RuntimeException {

    private HttpStatus httpStatus;

    public InvalidCommentDataException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
