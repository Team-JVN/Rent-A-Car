package jvn.Users.exceptionHandler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.List;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidTokenException.class)
    protected ResponseEntity<Object> handleInvalidTokenDataException(InvalidTokenException ex) {
        ErrorResponse error = new ErrorResponse(ex.getHttpStatus(), ex.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(InvalidRoleDataException.class)
    protected ResponseEntity<Object> handleInvalidRoleDataException(InvalidRoleDataException ex) {
        ErrorResponse error = new ErrorResponse(ex.getHttpStatus(), ex.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(InvalidPermissionDataException.class)
    protected ResponseEntity<Object> handleInvalidPermissionDataException(InvalidPermissionDataException ex) {
        ErrorResponse error = new ErrorResponse(ex.getHttpStatus(), ex.getMessage());
        return buildResponseEntity(error);
    }


    @ExceptionHandler(InvalidClientDataException.class)
    protected ResponseEntity<Object> handleInvalidClientDataException(InvalidClientDataException ex) {
        ErrorResponse error = new ErrorResponse(ex.getHttpStatus(), ex.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(InvalidAgentDataException.class)
    protected ResponseEntity<Object> handleInvalidAgentDataException(InvalidAgentDataException ex) {
        ErrorResponse error = new ErrorResponse(ex.getHttpStatus(), ex.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    protected ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(InvalidAdminDataException.class)
    protected ResponseEntity<Object> handleInvalidAdminDataException(InvalidAdminDataException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(InvalidUserDataException.class)
    protected ResponseEntity<Object> handleInvalidUserDataException(InvalidUserDataException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(InvalidPasswordDataException.class)
    protected ResponseEntity<Object> handleInvalidPasswordDataException(InvalidPasswordDataException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(BlockedUserException.class)
    protected ResponseEntity<Object> handleBlockedUserException(BlockedUserException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleOtherExceptions() {
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, "Unknown error occurred. Please try again.");
        return buildResponseEntity(error);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {

        StringBuilder stringBuilder = new StringBuilder();
        ex.getConstraintViolations().forEach(constraintViolation -> {
            stringBuilder.append(constraintViolation.getMessage());
            stringBuilder.append(" ");
        });

        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, stringBuilder.toString());
        return buildResponseEntity(error);
    }

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        final List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        StringBuilder stringBuilder = new StringBuilder();
        for (FieldError fieldError : fieldErrors) {
            stringBuilder.append(fieldError.getDefaultMessage());
            stringBuilder.append(" ");
        }

        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, stringBuilder.toString());
        return buildResponseEntity(error);
    }

    private ResponseEntity<Object> buildResponseEntity(ErrorResponse error) {
        return new ResponseEntity<>(error, error.getStatus());
    }
}

