package jvn.Users.controller;

import jvn.Users.dto.both.AgentDTO;
import jvn.Users.dto.both.ClientDTO;
import jvn.Users.dto.message.Log;
import jvn.Users.dto.request.ChangePasswordDTO;
import jvn.Users.dto.request.RequestTokenDTO;
import jvn.Users.dto.request.ResetPasswordDTO;
import jvn.Users.exceptionHandler.BlockedUserException;
import jvn.Users.exceptionHandler.InvalidTokenException;
import jvn.Users.exceptionHandler.InvalidUserDataException;
import jvn.Users.mapper.AgentDtoMapper;
import jvn.Users.mapper.ClientDtoMapper;
import jvn.Users.model.UserTokenState;
import jvn.Users.producer.LogProducer;
import jvn.Users.security.JwtAuthenticationRequest;
import jvn.Users.service.AgentService;
import jvn.Users.service.AuthentificationService;
import jvn.Users.service.ClientService;
import jvn.Users.service.UserService;
import jvn.Users.utils.IPAddressProvider;
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

    private AgentService agentService;

    private ClientDtoMapper clientDtoMapper;

    private AgentDtoMapper agentDtoMapper;

    private AuthentificationService authentificationService;

    private LogProducer logProducer;

    private IPAddressProvider ipAddressProvider;

    @PostMapping(value = "/login")
    public ResponseEntity<UserTokenState> login(@RequestBody JwtAuthenticationRequest authenticationRequest) {
        try {
            UserTokenState userTokenState = authentificationService.login(authenticationRequest);
            if (userTokenState == null) {
                logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "LGN", String.format("Invalid email or password provided from %s", ipAddressProvider.get())));
                throw new UsernameNotFoundException("Invalid email or password. Please try again.");
            }
            return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(userTokenState);
        } catch (AuthenticationException e) {
            if (authentificationService.userIsNeverLoggedIn(authenticationRequest.getUsername())) {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
            if (e.getMessage().equals("Blocked")) {
                logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "LGN", String.format("Because of too many attempts to login, user from %s is blocked.", ipAddressProvider.get())));
                throw new BlockedUserException("You tried to log in too many times. Your account wil be blocked for the next 24 hours.",
                        HttpStatus.BAD_REQUEST);
            }
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "LGN", String.format("Invalid email or password provided from %s", ipAddressProvider.get())));
            throw new UsernameNotFoundException("Invalid email or password. Please try again.");
        } catch (NullPointerException e) {
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "LGN", String.format("Invalid email or password provided from %s", ipAddressProvider.get())));
            throw new UsernameNotFoundException("Invalid email or password. Please try again.");
        }
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        try {
            authentificationService.changePassword(changePasswordDTO);
        } catch (NullPointerException e) {
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CPW", String.format("Invalid email or password provided from %s", ipAddressProvider.get())));
            throw new InvalidUserDataException("Invalid email or password. Please try again.", HttpStatus.BAD_REQUEST);
        } catch (NoSuchAlgorithmException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CPW", "External password check failed"));
            throw new InvalidUserDataException("Password cannot be checked. Please try again.", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(null);
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientDTO> register(@Valid @RequestBody ClientDTO clientDTO) {
        try {
            authentificationService.checkPassword(clientDTO.getPassword());
        } catch (NoSuchAlgorithmException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "REG", "External password check failed"));
            throw new InvalidUserDataException("Password cannot be check. Please try again.", HttpStatus.BAD_REQUEST);
        }
        try {
            return new ResponseEntity<>(
                    clientDtoMapper.toDto(clientService.create(clientDtoMapper.toEntity(clientDTO), false, null)),
                    HttpStatus.CREATED);
        } catch (NoSuchAlgorithmException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "REG", "Hash algorithm threw exception"));
            throw new InvalidTokenException("Activation token cannot be generated. Please try again.",
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/register-agent", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AgentDTO> registerAgent(@Valid @RequestBody AgentDTO agentDTO) {
        try {
            authentificationService.checkPassword(agentDTO.getPassword());
        } catch (NoSuchAlgorithmException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CAG", "External password check failed"));
            throw new InvalidUserDataException("Password cannot be check. Please try again.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(agentDtoMapper.toDto(agentService.create(agentDtoMapper.toEntity(agentDTO))),
                HttpStatus.CREATED);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> generateResetToken(@Valid @RequestBody RequestTokenDTO requestTokenDTO) {
        try {
            userService.generateResetToken(requestTokenDTO.getEmail());
        } catch (NoSuchAlgorithmException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "RST", "Hash algorithm threw exception"));
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
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "RPW", String.format("Invalid email or password provided from %s", ipAddressProvider.get())));
            throw new InvalidUserDataException("Invalid email or password.", HttpStatus.BAD_REQUEST);
        } catch (NoSuchAlgorithmException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "RPW", "External password check failed"));
            throw new InvalidUserDataException("Reset token or new password cannot be checked. Please try again.",
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public ResponseEntity<UserTokenState> refreshAuthenticationToken(HttpServletRequest request) {
        return new ResponseEntity<>(userService.refreshAuthenticationToken(request), HttpStatus.OK);
    }

    @Autowired
    public UserController(UserService userService, ClientService clientService, ClientDtoMapper clientDtoMapper,
                          AuthentificationService authentificationService, AgentDtoMapper agentDtoMapper,
                          AgentService agentService, LogProducer logProducer, IPAddressProvider ipAddressProvider) {

        this.userService = userService;
        this.clientService = clientService;
        this.clientDtoMapper = clientDtoMapper;
        this.authentificationService = authentificationService;
        this.agentDtoMapper = agentDtoMapper;
        this.agentService = agentService;
        this.logProducer = logProducer;
        this.ipAddressProvider = ipAddressProvider;
    }
}
