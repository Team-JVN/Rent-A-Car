package jvn.Users.controller;

import jvn.Users.dto.response.UserDTO;
import jvn.Users.mapper.UserDtoMapper;
import jvn.Users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/verify", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private UserService userService;

    private UserDtoMapper userDtoMapper;

    @GetMapping
    public ResponseEntity<UserDTO> verify() {
        return new ResponseEntity<>(userDtoMapper.toDto(this.userService.verify()), HttpStatus.OK);
    }

    @Autowired
    public AuthController(UserService userService, UserDtoMapper userDtoMapper) {
        this.userService = userService;
        this.userDtoMapper = userDtoMapper;
    }
}
