package jvn.RentACar.controller;

import jvn.RentACar.dto.both.ClientDTO;
import jvn.RentACar.dto.request.ChangePasswordDTO;
import jvn.RentACar.exceptionHandler.BlockedUserException;
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
        } catch (AuthenticationException  e) {
            if(authentificationService.userIsNeverLoggedIn(authenticationRequest.getUsername())){
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
            if(e.getMessage().equals("Blocked")){
                throw new BlockedUserException("You are blocked so you can not sign in.",HttpStatus.BAD_REQUEST);
            }
            throw new UsernameNotFoundException("Invalid email or password. Please try again.");
        }catch (NullPointerException e){
            throw new UsernameNotFoundException("Invalid email or password. Please try again.");
        }
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        try {
            authentificationService.changePassword(changePasswordDTO);
        } catch (NullPointerException e) {
            throw new InvalidUserDataException("Invalid email or password.", HttpStatus.BAD_REQUEST);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new InvalidUserDataException("Password can not be check. Please try again.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientDTO> register(@Valid @RequestBody ClientDTO clientDTO) {
        try {
            authentificationService.checkPassword(clientDTO.getPassword());
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new InvalidUserDataException("Password can not be check. Please try again.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(clientDtoMapper.toDto(clientService.create(clientDtoMapper.toEntity(clientDTO))),
                HttpStatus.CREATED);
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
