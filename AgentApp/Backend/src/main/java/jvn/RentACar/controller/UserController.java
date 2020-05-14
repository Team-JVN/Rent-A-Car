package jvn.RentACar.controller;

import jvn.RentACar.dto.both.ClientDTO;
import jvn.RentACar.dto.request.ChangePasswordDTO;
import jvn.RentACar.dto.response.LoggedInUserDTO;
import jvn.RentACar.exceptionHandler.InvalidUserDataException;
import jvn.RentACar.mapper.ClientDtoMapper;
import jvn.RentACar.security.JwtAuthenticationRequest;
import jvn.RentACar.service.ClientService;
import jvn.RentACar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private UserService userService;

    private ClientService clientService;

    private ClientDtoMapper clientDtoMapper;

    @PostMapping(value = "/login")
    public ResponseEntity<LoggedInUserDTO> login(@RequestBody JwtAuthenticationRequest authenticationRequest) {
        try {
            LoggedInUserDTO loggedInUserDTO = userService.login(authenticationRequest);
            if (loggedInUserDTO == null) {
                throw new UsernameNotFoundException(String.format("Invalid email or password. Please try again."));
            }
            return new ResponseEntity<>(loggedInUserDTO, HttpStatus.OK);
        } catch (AuthenticationException | NullPointerException e) {
            throw new UsernameNotFoundException(String.format("Invalid email or password. Please try again."));
        }
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        try {
            userService.changePassword(changePasswordDTO);
        } catch (NullPointerException e) {
            throw new InvalidUserDataException("Invalid email or password.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientDTO> register(@Valid @RequestBody ClientDTO clientDTO) {
        return new ResponseEntity<>(clientDtoMapper.toDto(clientService.create(clientDtoMapper.toEntity(clientDTO))), HttpStatus.CREATED);
    }

    @Autowired
    public UserController(UserService userService, ClientService clientService, ClientDtoMapper clientDtoMapper) {
        this.userService = userService;
        this.clientService = clientService;
        this.clientDtoMapper = clientDtoMapper;
    }
}
