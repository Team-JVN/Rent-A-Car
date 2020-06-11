package jvn.RentACar.controller;

import jvn.RentACar.dto.both.ClientDTO;
import jvn.RentACar.exceptionHandler.InvalidClientDataException;
import jvn.RentACar.exceptionHandler.InvalidTokenException;
import jvn.RentACar.mapper.ClientDtoMapper;
import jvn.RentACar.model.Client;
import jvn.RentACar.model.User;
import jvn.RentACar.service.ClientService;
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
            return new ResponseEntity<>(clientDtoMapper.toDto(clientService.edit(userService.getLoginUser().getId(), clientDtoMapper.toEntity(clientDTO))), HttpStatus.OK);
        }
        throw new InvalidClientDataException("As a non-authorized user, you are not allowed to enter this page.", HttpStatus.FORBIDDEN);
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

    @GetMapping(value = "/profile")
    public ResponseEntity<ClientDTO> getProfile() {
        User user = userService.getLoginUser();
        if (user instanceof Client) {
            return new ResponseEntity<>(clientDtoMapper.toDto((Client) userService.getLoginUser()), HttpStatus.OK);
        }
        throw new InvalidClientDataException("As a non-authorized user, you are not allowed to enter this page.", HttpStatus.FORBIDDEN);
    }

    @Autowired
    public ClientController(ClientService clientService, ClientDtoMapper clientDtoMapper,UserService userService) {
        this.clientService = clientService;
        this.clientDtoMapper = clientDtoMapper;
        this.userService = userService;
    }
}
