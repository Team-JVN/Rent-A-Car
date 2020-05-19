package jvn.RentACar.controller;

import jvn.RentACar.dto.both.ClientDTO;
import jvn.RentACar.dto.request.ChangePasswordDTO;
import jvn.RentACar.dto.request.RequestTokenDTO;
import jvn.RentACar.dto.request.ResetPasswordDTO;
import jvn.RentACar.exceptionHandler.InvalidUserDataException;
import jvn.RentACar.mapper.ClientDtoMapper;
import jvn.RentACar.model.UserTokenState;
import jvn.RentACar.security.JwtAuthenticationRequest;
import jvn.RentACar.service.AuthentificationService;
import jvn.RentACar.service.ClientService;
import jvn.RentACar.service.UserService;
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
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private UserService userService;

    private ClientService clientService;

    private ClientDtoMapper clientDtoMapper;

    private AuthentificationService authentificationService;

    @PostMapping(value = "/login")
    public ResponseEntity<UserTokenState> login(@RequestBody JwtAuthenticationRequest authenticationRequest) {
        try {
            UserTokenState userTokenState = authentificationService.login(authenticationRequest);
            if (userTokenState == null) {
                throw new UsernameNotFoundException(String.format("Invalid email or password. Please try again."));
            }
            return new ResponseEntity<>(userTokenState, HttpStatus.OK);
        } catch (AuthenticationException | NullPointerException e) {
            throw new UsernameNotFoundException(String.format("Invalid email or password. Please try again."));
        }
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        try {
            authentificationService.changePassword(changePasswordDTO);
        } catch (NullPointerException e) {
            throw new InvalidUserDataException("Invalid email or password.", HttpStatus.BAD_REQUEST);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new InvalidUserDataException("Password cannot be check. Please try again.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientDTO> register(@Valid @RequestBody ClientDTO clientDTO) {
        try {
            authentificationService.checkPassword(clientDTO.getPassword());
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new InvalidUserDataException("Password can not be check. Please try again.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(clientDtoMapper.toDto(clientService.create(clientDtoMapper.toEntity(clientDTO))),
                HttpStatus.CREATED);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> generateResetToken(@Valid @RequestBody RequestTokenDTO requestTokenDTO) {
        userService.generateResetToken(requestTokenDTO.getEmail());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = "/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> resetPassword(
            @RequestParam @Pattern(regexp = "^([0-9a-fA-F]{8})-(([0-9a-fA-F]{4}-){3})([0-9a-fA-F]{12})$",
                    message = "This reset token is invalid.") String t, @Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        try {
            authentificationService.resetPassword(t, resetPasswordDTO);
        } catch (NullPointerException e) {
            throw new InvalidUserDataException("Invalid email or password.", HttpStatus.BAD_REQUEST);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new InvalidUserDataException("Password cannot be check. Please try again.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public ResponseEntity<UserTokenState> refreshAuthenticationToken(HttpServletRequest request) {
        return new ResponseEntity<>(userService.refreshAuthenticationToken(request), HttpStatus.OK);
    }

    @Autowired
    public UserController(UserService userService, ClientService clientService, ClientDtoMapper clientDtoMapper,
                          AuthentificationService authentificationService) {
        this.userService = userService;
        this.clientService = clientService;
        this.clientDtoMapper = clientDtoMapper;
        this.authentificationService = authentificationService;
    }
}
