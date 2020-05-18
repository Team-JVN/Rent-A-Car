package jvn.Cars.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.springframework.web.bind.annotation.*;

@RestController
public class CarController {

    @GetMapping("/hello")
    public ResponseEntity<?> get() throws UnknownHostException {
        String ip = InetAddress.getLocalHost().getHostAddress();
        return new ResponseEntity<>(String.format("Hello from service with ip address %s!", ip), HttpStatus.OK);
    }
}
