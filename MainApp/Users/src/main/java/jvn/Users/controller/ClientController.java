package jvn.Users.controller;

import jvn.Users.dto.both.ClientDTO;
import jvn.Users.enumeration.ClientStatus;
import jvn.Users.exceptionHandler.InvalidClientDataException;
import jvn.Users.exceptionHandler.InvalidTokenException;
import jvn.Users.mapper.ClientDtoMapper;
import jvn.Users.model.Client;
import jvn.Users.model.User;
import jvn.Users.service.ClientService;
import jvn.Users.service.UserService;
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

    private ClientService clientService;

    private ClientDtoMapper clientDtoMapper;

    private UserService userService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientDTO> create(@Valid @RequestBody ClientDTO clientDTO) {
        try {
            return new ResponseEntity<>(clientDtoMapper.toDto(clientService.create(clientDtoMapper.toEntity(clientDTO))), HttpStatus.CREATED);
        } catch (NoSuchAlgorithmException e) {
            throw new InvalidTokenException("Activation token cannot be generated. Please try again.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ClientDTO> get(@PathVariable("id") @Positive(message = "Id must be positive.") Long id) {
        return new ResponseEntity<>(clientDtoMapper.toDto(clientService.get(id, ClientStatus.ACTIVE)), HttpStatus.OK);
    }

    @GetMapping(value = "/for-rent-request")
    public ResponseEntity<List<ClientDTO>> getForRentRequest() {
        User user = userService.getLoginUser();
        List<ClientDTO> list = clientService.getForRentRequest(user.getId()).stream().map(clientDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping(value = "/verify/{clientId}")
    public ResponseEntity<?> verify(@PathVariable("clientId") @Positive(message = "Id must be positive.") Long clientId) {
        return new ResponseEntity<>(clientService.verify(clientId), HttpStatus.OK);
    }

    @GetMapping("/all/{status}")
    public ResponseEntity<List<ClientDTO>> getAll(
            @PathVariable(value = "status") @Pattern(regexp = "(?i)(all|awaiting|approved|active|blocked)$", message = "Status is not valid.") String status) {
        List<ClientDTO> list = clientService.get(status, userService.getLoginUser().getId()).stream().map(clientDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") @Positive(message = "Id must be positive.") Long id) {
        clientService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "/activate")
    public ResponseEntity<ClientDTO> activateAccount(
            @RequestParam @Pattern(regexp = "^([0-9a-fA-F]{8})-(([0-9a-fA-F]{4}-){3})([0-9a-fA-F]{12})$",
                    message = "This activation link is invalid.") String t) {
        try {
            return new ResponseEntity<>(clientDtoMapper.toDto(clientService.activateAccount(t)), HttpStatus.OK);
        } catch (NoSuchAlgorithmException e) {
            throw new InvalidTokenException("Activation token cannot be checked. Please try again.", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/{id}/approve")
    public ResponseEntity<ClientDTO> approveRequestToRegister(@PathVariable @Positive(message = "Id must be positive.") Long id) {
        try {
            return new ResponseEntity<>(clientDtoMapper.toDto(clientService.approveRequestToRegister(id)), HttpStatus.OK);
        } catch (NoSuchAlgorithmException e) {
            throw new InvalidTokenException("Activation token cannot be checked. Please try again.", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/{id}/reject")
    public ResponseEntity<ClientDTO> rejectRequestToRegister(@PathVariable @Positive(message = "Id must be positive.") Long id,
                                                             @RequestBody @Pattern(regexp = "^[\\p{N}\\p{L}\\p{Sc}@ !()-,.:;/'\"&*=+%]+$", message = "Comment is not valid.") String reason) {
        return new ResponseEntity<>(clientDtoMapper.toDto(clientService.rejectRequestToRegister(id, reason)), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/block")
    public ResponseEntity<ClientDTO> block(@PathVariable @Positive(message = "Id must be positive.") Long id) {
        return new ResponseEntity<>(clientDtoMapper.toDto(clientService.block(id)), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/unblock")
    public ResponseEntity<ClientDTO> unblock(@PathVariable @Positive(message = "Id must be positive.") Long id) {
        return new ResponseEntity<>(clientDtoMapper.toDto(clientService.unblock(id)), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/create-rent-requests/{status}")
    public ResponseEntity<ClientDTO> createRentRequests(@PathVariable("id") @Positive(message = "Id must be positive.") Long id,
                                                        @PathVariable("status") @Pattern(regexp = "(?i)(disable|enable)$", message = "Status is not valid.") String status) {
        return new ResponseEntity<>(clientDtoMapper.toDto(clientService.createRentRequests(id, status)), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/create-comments/{status}")
    public ResponseEntity<ClientDTO> createComments(@PathVariable("id") @Positive(message = "Id must be positive.") Long id,
                                                    @PathVariable("status") @Pattern(regexp = "(?i)(disable|enable)$", message = "Status is not valid.") String status) {
        return new ResponseEntity<>(clientDtoMapper.toDto(clientService.createComments(id, status)), HttpStatus.OK);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientDTO> edit(@Valid @RequestBody ClientDTO clientDTO) {
        User user = userService.getLoginUser();
        if (user instanceof Client) {
            return new ResponseEntity<>(clientDtoMapper.toDto(clientService.edit(userService.getLoginUser().getId(), clientDtoMapper.toEntity(clientDTO))), HttpStatus.OK);
        }
        throw new InvalidClientDataException("As a non-authorized user, you are not allowed to enter this page.", HttpStatus.FORBIDDEN);
    }

    @GetMapping(value = "/logged-in-user")
    public ResponseEntity<ClientDTO> get() {
        User user = userService.getLoginUser();
        if (user instanceof Client) {
            return new ResponseEntity<>(clientDtoMapper.toDto((Client) userService.getLoginUser()), HttpStatus.OK);
        }
        throw new InvalidClientDataException("As a non-authorized user, you are not allowed to enter this page.", HttpStatus.FORBIDDEN);
    }

    @GetMapping("/clients-by-id/{clientId}")
    public ResponseEntity<List<ClientDTO>> getClientsById(@PathVariable("clientId") List<Long> clients) {
        List<ClientDTO> list = clientService.getClientsById(clients).stream().map(clientDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @Autowired
    public ClientController(ClientService clientService, ClientDtoMapper clientDtoMapper, UserService userService) {
        this.clientService = clientService;
        this.clientDtoMapper = clientDtoMapper;
        this.userService = userService;
    }
}
