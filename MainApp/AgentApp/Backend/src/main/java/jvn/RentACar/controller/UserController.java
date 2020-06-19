package jvn.RentACar.controller;

import jvn.RentACar.dto.both.ClientDTO;
import jvn.RentACar.dto.request.ChangePasswordDTO;
import jvn.RentACar.exceptionHandler.BlockedUserException;
import jvn.RentACar.dto.request.RequestTokenDTO;
import jvn.RentACar.dto.request.ResetPasswordDTO;
import jvn.RentACar.exceptionHandler.InvalidTokenException;
import jvn.RentACar.exceptionHandler.InvalidUserDataException;
import jvn.RentACar.mapper.ClientDtoMapper;
import jvn.RentACar.model.Log;
import jvn.RentACar.model.UserTokenState;
import jvn.RentACar.security.JwtAuthenticationRequest;
import jvn.RentACar.service.AuthentificationService;
import jvn.RentACar.service.ClientService;
import jvn.RentACar.service.LogService;
import jvn.RentACar.service.UserService;
import jvn.RentACar.utils.IPAddressProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
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

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private UserService userService;

    private ClientService clientService;

    private ClientDtoMapper clientDtoMapper;

    private AuthentificationService authentificationService;

    private LogService logService;

    private IPAddressProvider ipAddressProvider;

    @PostMapping(value = "/login")
    public ResponseEntity<UserTokenState> login(@RequestBody JwtAuthenticationRequest authenticationRequest) {
        try {
            UserTokenState userTokenState = authentificationService.login(authenticationRequest);
            if (userTokenState == null) {
                logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "LGN", String.format("Invalid email or password provided from %s", ipAddressProvider.get())));
                throw new UsernameNotFoundException("Invalid email or password. Please try again.");
            }
            return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(userTokenState);
        } catch (AuthenticationException e) {
            if (authentificationService.userIsNeverLoggedIn(authenticationRequest.getUsername())) {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
            if (e.getMessage().equals("Blocked")) {
                logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "LGN", String.format("User from %s is blocked.", ipAddressProvider.get())));
                throw new BlockedUserException(
                        "You tried to log in too many times. Your account wil be blocked for the next 24 hours.",
                        HttpStatus.BAD_REQUEST);
            }
            logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "LGN", String.format("Invalid email or password provided from %s", ipAddressProvider.get())));
            throw new UsernameNotFoundException("Invalid email or password. Please try again.");
        } catch (NullPointerException e) {
            logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "LGN", String.format("Invalid email or password provided from %s", ipAddressProvider.get())));
            throw new UsernameNotFoundException("Invalid email or password. Please try again.");
        }
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        try {
            authentificationService.changePassword(changePasswordDTO);
        } catch (NullPointerException e) {
            logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CPW", String.format("Invalid email or password provided from %s", ipAddressProvider.get())));
            throw new InvalidUserDataException("Invalid email or password.", HttpStatus.BAD_REQUEST);
        } catch (NoSuchAlgorithmException e) {
            logService.write(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CPW", "External password check failed"));
            throw new InvalidUserDataException("Password cannot be checked. Please try again.", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(null);
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientDTO> register(@Valid @RequestBody ClientDTO clientDTO) {
        try {
            authentificationService.checkPassword(clientDTO.getPassword());
        } catch (NoSuchAlgorithmException e) {
            logService.write(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "REG", "External password check failed"));
            throw new InvalidUserDataException("Password cannot be check. Please try again.", HttpStatus.BAD_REQUEST);
        }
        try {
            return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(clientDtoMapper.toDto(clientService.create(clientDtoMapper.toEntity(clientDTO))));
        } catch (NoSuchAlgorithmException e) {
            logService.write(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "REG", "Hash algorithm threw exception"));
            throw new InvalidTokenException("Activation token cannot be generated. Please try again.",
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> generateResetToken(@Valid @RequestBody RequestTokenDTO requestTokenDTO) {
        try {
            userService.generateResetToken(requestTokenDTO.getEmail());
        } catch (NoSuchAlgorithmException e) {
            logService.write(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "RST", "Hash algorithm threw exception"));
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
            logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "RPW", String.format("Invalid email or password provided from %s", ipAddressProvider.get())));
            throw new InvalidUserDataException("Invalid email or password.", HttpStatus.BAD_REQUEST);
        } catch (NoSuchAlgorithmException e) {
            logService.write(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "RPW", "External password check failed"));
            throw new InvalidUserDataException("Reset token or new password cannot be checked. Please try again.",
                    HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(null);
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public ResponseEntity<UserTokenState> refreshAuthenticationToken(HttpServletRequest request) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(userService.refreshAuthenticationToken(request));
    }

    @Autowired
    public UserController(UserService userService, ClientService clientService, ClientDtoMapper clientDtoMapper,
                          AuthentificationService authentificationService, LogService logService, IPAddressProvider ipAddressProvider) {
        this.userService = userService;
        this.clientService = clientService;
        this.clientDtoMapper = clientDtoMapper;
        this.authentificationService = authentificationService;
        this.logService = logService;
        this.ipAddressProvider = ipAddressProvider;
    }
}
