package jvn.Users.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @RequestMapping("/health")
    public String home() {
        return "Hello world";
    }

    @RequestMapping("/hello")
    public String hello() {
        return "Hello from User Service";
    }
}
