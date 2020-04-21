package jvn.RentACar.exceptionHandler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidBodyStyleDataException.class)
    protected ResponseEntity<Object> handleInvalidBodyStyleDataException(InvalidBodyStyleDataException ex) {
        ErrorResponse error = new ErrorResponse(ex.getHttpStatus(), ex.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity<Object> handleNullPointerException(NullPointerException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, "Please try again.");
        return buildResponseEntity(error);
    }

    @ExceptionHandler(InvalidFuelTypeDataException.class)
    protected ResponseEntity<Object> handleInvalidFuelTypeDataException(InvalidFuelTypeDataException ex) {
        ErrorResponse error = new ErrorResponse(ex.getHttpStatus(), ex.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(InvalidGearBoxTypeDataException.class)
    protected ResponseEntity<Object> handleInvalidGearBoxTypeDataException(InvalidGearBoxTypeDataException ex) {
        ErrorResponse error = new ErrorResponse(ex.getHttpStatus(), ex.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(InvalidCarDataException.class)
    protected ResponseEntity<Object> handleInvalidCarDataException(InvalidCarDataException ex) {
        ErrorResponse error = new ErrorResponse(ex.getHttpStatus(), ex.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(FileNotFoundException.class)
    protected ResponseEntity<Object> handleFileNotFoundException(FileNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(ex.getHttpStatus(), ex.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(InvalidPriceListDataException.class)
    protected ResponseEntity<Object> handleInvalidPriceListDataException(InvalidPriceListDataException ex) {
        ErrorResponse error = new ErrorResponse(ex.getHttpStatus(), ex.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(InvalidAdvertisementDataException.class)
    protected ResponseEntity<Object> handleInvalidAdvertisementDataException(InvalidAdvertisementDataException ex) {
        ErrorResponse error = new ErrorResponse(ex.getHttpStatus(), ex.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(InvalidClientDataException.class)
    protected ResponseEntity<Object> handleInvalidClientDataException(InvalidClientDataException ex) {
        ErrorResponse error = new ErrorResponse(ex.getHttpStatus(), ex.getMessage());
        return buildResponseEntity(error);
    }

    private ResponseEntity<Object> buildResponseEntity(ErrorResponse error) {
        return new ResponseEntity<>(error, error.getStatus());
    }
}
