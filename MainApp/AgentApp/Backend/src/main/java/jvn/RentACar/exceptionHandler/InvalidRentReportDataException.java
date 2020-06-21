package jvn.RentACar.exceptionHandler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class InvalidRentReportDataException extends RuntimeException {

    private HttpStatus httpStatus;

    public InvalidRentReportDataException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
