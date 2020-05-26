package jvn.Renting.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RentRequestController {
    @RequestMapping("/health")
    public String home() {
        return "Hello world";
    }

    @RequestMapping("/hello")
    public String hello() {
        return "Hello from Renting Service";
    }
}
