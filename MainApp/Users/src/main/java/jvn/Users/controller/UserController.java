package jvn.Users.controller;

import jvn.Users.dto.both.ClientDTO;
import jvn.Users.dto.request.ChangePasswordDTO;
import jvn.Users.dto.response.UserDTO;
import jvn.Users.exceptionHandler.BlockedUserException;
import jvn.Users.dto.request.RequestTokenDTO;
import jvn.Users.dto.request.ResetPasswordDTO;
import jvn.Users.exceptionHandler.InvalidTokenException;
import jvn.Users.exceptionHandler.InvalidUserDataException;
import jvn.Users.mapper.ClientDtoMapper;
import jvn.Users.mapper.UserDtoMapper;
import jvn.Users.model.UserTokenState;
import jvn.Users.security.JwtAuthenticationRequest;
import jvn.Users.service.AuthentificationService;
import jvn.Users.service.ClientService;
import jvn.Users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private UserService userService;

    private ClientService clientService;

    private ClientDtoMapper clientDtoMapper;

    private AuthentificationService authentificationService;

    private UserDtoMapper userDtoMapper;

    @RequestMapping("/health")
    public String health() {
        return "Hello world";
    }

    @PostMapping(value = "/login")
    public ResponseEntity<UserTokenState> login(@RequestBody JwtAuthenticationRequest authenticationRequest) {
        try {
            UserTokenState userTokenState = authentificationService.login(authenticationRequest);
            if (userTokenState == null) {
                throw new UsernameNotFoundException(String.format("Invalid email or password. Please try again."));
            }
            return new ResponseEntity<>(userTokenState, HttpStatus.OK);
        } catch (AuthenticationException e) {
            if (authentificationService.userIsNeverLoggedIn(authenticationRequest.getUsername())) {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
            if (e.getMessage().equals("Blocked")) {
                throw new BlockedUserException(
                        "You tried to log in too many times. Your account wil be blocked for the next 24 hours.",
                        HttpStatus.BAD_REQUEST);
            }
            throw new UsernameNotFoundException("Invalid email or password. Please try again.");
        } catch (NullPointerException e) {
            throw new UsernameNotFoundException("Invalid email or password. Please try again.");
        }
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        try {
            authentificationService.changePassword(changePasswordDTO);
        } catch (NullPointerException e) {
            throw new InvalidUserDataException("Invalid email or password.", HttpStatus.BAD_REQUEST);
        } catch (NoSuchAlgorithmException e) {
            throw new InvalidUserDataException("Password cannot be checked. Please try again.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientDTO> register(@Valid @RequestBody ClientDTO clientDTO) {
        try {
            authentificationService.checkPassword(clientDTO.getPassword());
        } catch (NoSuchAlgorithmException e) {
            throw new InvalidUserDataException("Password cannot be check. Please try again.", HttpStatus.BAD_REQUEST);
        }
        try {
            return new ResponseEntity<>(
                    clientDtoMapper.toDto(clientService.create(clientDtoMapper.toEntity(clientDTO))),
                    HttpStatus.CREATED);
        } catch (NoSuchAlgorithmException e) {
            throw new InvalidTokenException("Activation token cannot be generated. Please try again.",
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> generateResetToken(@Valid @RequestBody RequestTokenDTO requestTokenDTO) {
        try {
            userService.generateResetToken(requestTokenDTO.getEmail());
        } catch (NoSuchAlgorithmException e) {
            throw new InvalidTokenException("Reset token cannot be generated. Please try again.",
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = "/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> resetPassword(
            @RequestParam @Pattern(regexp = "^([0-9a-fA-F]{8})-(([0-9a-fA-F]{4}-){3})([0-9a-fA-F]{12})$", message = "This reset token is invalid.") String t,
            @Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        try {
            authentificationService.resetPassword(t, resetPasswordDTO);
        } catch (NullPointerException e) {
            throw new InvalidUserDataException("Invalid email or password.", HttpStatus.BAD_REQUEST);
        } catch (NoSuchAlgorithmException e) {
            throw new InvalidUserDataException("Reset token or new password cannot be checked. Please try again.",
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public ResponseEntity<UserTokenState> refreshAuthenticationToken(HttpServletRequest request) {
        return new ResponseEntity<>(userService.refreshAuthenticationToken(request), HttpStatus.OK);
    }

//    @GetMapping("/verify/{jwt-token}")
//    public ResponseEntity<UserDTO> verify(@PathVariable("jwt-token") String jwtToken) {
//        return new ResponseEntity<>(userDtoMapper.toDto(this.userService.verify(jwtToken)), HttpStatus.OK);
//    }

    @Autowired
    public UserController(UserService userService, ClientService clientService, ClientDtoMapper clientDtoMapper,
                          AuthentificationService authentificationService,UserDtoMapper userDtoMapper) {
        this.userService = userService;
        this.clientService = clientService;
        this.clientDtoMapper = clientDtoMapper;
        this.authentificationService = authentificationService;
        this.userDtoMapper = userDtoMapper;
    }
}
