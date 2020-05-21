package jvn.Cars.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CarController {

    @RequestMapping("/health")
    public String home() {
        return "Hello world";
    }

    @RequestMapping("/hello")
    public String hello() {
        return "Hello world 2";
    }

    @GetMapping("/verify/{email}")
    public ResponseEntity<?> verify(@PathVariable("email") String email) {
        System.out.println("Verification invoked!");
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
