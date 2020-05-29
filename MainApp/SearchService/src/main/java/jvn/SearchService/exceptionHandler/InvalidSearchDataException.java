package jvn.SearchService.exceptionHandler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class InvalidSearchDataException extends RuntimeException {

    private HttpStatus httpStatus;

    public InvalidSearchDataException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}

