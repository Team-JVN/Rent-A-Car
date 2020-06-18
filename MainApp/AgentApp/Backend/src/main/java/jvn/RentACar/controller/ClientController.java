package jvn.RentACar.controller;

import jvn.RentACar.dto.both.ClientDTO;
import jvn.RentACar.exceptionHandler.InvalidClientDataException;
import jvn.RentACar.exceptionHandler.InvalidTokenException;
import jvn.RentACar.mapper.ClientDtoMapper;
import jvn.RentACar.model.Client;
import jvn.RentACar.model.Log;
import jvn.RentACar.model.User;
import jvn.RentACar.service.ClientService;
import jvn.RentACar.service.LogService;
import jvn.RentACar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping(value = "/api/client", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientController {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private ClientService clientService;

    private ClientDtoMapper clientDtoMapper;

    private UserService userService;

    private LogService logService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientDTO> create(@Valid @RequestBody ClientDTO clientDTO) {
        try {
            Client client = clientService.create(clientDtoMapper.toEntity(clientDTO));
            if (clientDTO.getPassword() == null) {
                logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CCL", String.format("User %s successfully created client %s", userService.getLoginUser().getId(), client.getId())));
            } else {
                logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "REG", String.format("User %s successfully registered", client.getId())));
            }
            return new ResponseEntity<>(clientDtoMapper.toDto(client), HttpStatus.CREATED);
        } catch (NoSuchAlgorithmException e) {
            logService.write(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CCL", "Hash algorithm threw exception"));
            throw new InvalidTokenException("Activation token cannot be generated. Please try again.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<ClientDTO>> get() {
        List<ClientDTO> list = clientService.get().stream().map(clientDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientDTO> edit(@Valid @RequestBody ClientDTO clientDTO) {
        User user = userService.getLoginUser();
        if (user instanceof Client) {
            Client client = clientService.edit(userService.getLoginUser().getId(), clientDtoMapper.toEntity(clientDTO));
            logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "ECL", String.format("User %s successfully edited profile", client.getId())));
            return new ResponseEntity<>(clientDtoMapper.toDto(client), HttpStatus.OK);
        }
        throw new InvalidClientDataException("As a non-authorized user, you are not allowed to enter this page.", HttpStatus.FORBIDDEN);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") @Positive(message = "Id must be positive.") Long id) {
        clientService.delete(id);
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "DCL", String.format("User %s successfully deleted client %s", userService.getLoginUser().getId(), id)));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "/activate")
    public ResponseEntity<ClientDTO> activateAccount(
            @RequestParam @Pattern(regexp = "^([0-9a-fA-F]{8})-(([0-9a-fA-F]{4}-){3})([0-9a-fA-F]{12})$",
                    message = "This activation link is invalid.") String t) {
        try {
            Client client = clientService.activateAccount(t);
            logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "ACT", String.format("User %s successfully activated account", client.getId())));
            return new ResponseEntity<>(clientDtoMapper.toDto(client), HttpStatus.OK);
        } catch (NoSuchAlgorithmException e) {
            logService.write(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "ACT", "Hash algorithm threw exception"));
            throw new InvalidTokenException("Activation token cannot be checked. Please try again.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/profile")
    public ResponseEntity<ClientDTO> getProfile() {
        User user = userService.getLoginUser();
        if (user instanceof Client) {
            return new ResponseEntity<>(clientDtoMapper.toDto((Client) userService.getLoginUser()), HttpStatus.OK);
        }
        throw new InvalidClientDataException("As a non-authorized user, you are not allowed to enter this page.", HttpStatus.FORBIDDEN);
    }

    @Autowired
    public ClientController(ClientService clientService, ClientDtoMapper clientDtoMapper, UserService userService, LogService logService) {
        this.clientService = clientService;
        this.clientDtoMapper = clientDtoMapper;
        this.userService = userService;
        this.logService = logService;
    }
}
