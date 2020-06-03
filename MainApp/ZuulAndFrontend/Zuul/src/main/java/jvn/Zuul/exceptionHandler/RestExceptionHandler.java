package jvn.Zuul.exceptionHandler;

import feign.FeignException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(InvalidUserDataException.class)
    protected ResponseEntity<Object> handleInvalidUserDataException(InvalidUserDataException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        return buildResponseEntity(error);
    }

    private ResponseEntity<Object> buildResponseEntity(ErrorResponse error) {
        return new ResponseEntity<>(error, error.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleOtherExceptions() {
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, "Unknown error occurred. Please try again.");
        return buildResponseEntity(error);
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<?> handleFeignNotFoundException(FeignException e,
                                                          HttpServletResponse response) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FeignException.Unauthorized.class)
    public ResponseEntity<?> handleFeignUnauthorizedException(FeignException e,
                                                              HttpServletResponse response) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}
