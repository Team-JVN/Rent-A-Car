package jvn.Users.controller;

import jvn.Users.dto.both.ClientDTO;
import jvn.Users.exceptionHandler.InvalidTokenException;
import jvn.Users.mapper.ClientDtoMapper;
import jvn.Users.service.ClientService;
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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientDTO> create(@Valid @RequestBody ClientDTO clientDTO) {
        try {
            return new ResponseEntity<>(clientDtoMapper.toDto(clientService.create(clientDtoMapper.toEntity(clientDTO))), HttpStatus.CREATED);
        } catch (NoSuchAlgorithmException e) {
            throw new InvalidTokenException("Activation token cannot be generated. Please try again.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all/{status}")
    public ResponseEntity<List<ClientDTO>> getAll(
            @PathVariable(value = "status", required = false) @Pattern(regexp = "(?i)(all|awaiting|approved|active|blocked)$", message = "Status is not valid.") String status) {
        List<ClientDTO> list = clientService.get(status).stream().map(clientDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

//    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<ClientDTO> edit(@PathVariable @Positive(message = "Id must be positive.") Long id, @Valid @RequestBody ClientDTO clientDTO) {
//        return new ResponseEntity<>(clientDtoMapper.toDto(clientService.edit(id, clientDtoMapper.toEntity(clientDTO))), HttpStatus.OK);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable("id") @Positive(message = "Id must be positive.") Long id) {
//        clientService.delete(id);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }

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

    @Autowired
    public ClientController(ClientService clientService, ClientDtoMapper clientDtoMapper) {
        this.clientService = clientService;
        this.clientDtoMapper = clientDtoMapper;
    }
}
