package jvn.Zuul.exceptionHandler;

import com.netflix.client.ClientException;
import com.netflix.zuul.exception.ZuulException;
import feign.FeignException;
import jvn.Zuul.dto.message.Log;
import jvn.Zuul.producer.LogProducer;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    @Autowired
    private LogProducer logProducer;

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
        logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "FGN", "Feign client is not responding"));
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FeignException.Unauthorized.class)
    public ResponseEntity<?> handleFeignUnauthorizedException(FeignException e,
                                                              HttpServletResponse response) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ZuulException.class)
    public ResponseEntity<?> handleZuulException(ZuulException e,
                                                 HttpServletResponse response) {
        return new ResponseEntity<>("Something goes wrong.Please try again.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ClientException.class)
    public ResponseEntity<?> handleClientException(ClientException e,
                                                   HttpServletResponse response) {
        return new ResponseEntity<>("Something goes wrong.Please try again.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalStateException(IllegalStateException e,
                                                         HttpServletResponse response) {
        return new ResponseEntity<>("Something goes wrong.Please try again.", HttpStatus.BAD_REQUEST);
    }


}
