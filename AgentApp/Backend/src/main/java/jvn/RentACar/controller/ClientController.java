package jvn.RentACar.controller;

import jvn.RentACar.dto.both.ClientDTO;
import jvn.RentACar.mapper.ClientDtoMapper;
import jvn.RentACar.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/client", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientController {

    private ClientService clientService;

    private ClientDtoMapper clientDtoMapper;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientDTO> create(@Valid @RequestBody ClientDTO clientDTO) {
        return new ResponseEntity<>(clientDtoMapper.toDto(clientService.create(clientDtoMapper.toEntity(clientDTO))), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ClientDTO>> get() {
        List<ClientDTO> list = clientService.get().stream().map(clientDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientDTO> edit(@PathVariable Long id, @Valid @RequestBody ClientDTO clientDTO) {
        return new ResponseEntity<>(clientDtoMapper.toDto(clientService.edit(id, clientDtoMapper.toEntity(clientDTO))), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        clientService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Autowired
    public ClientController(ClientService clientService, ClientDtoMapper clientDtoMapper) {
        this.clientService = clientService;
        this.clientDtoMapper = clientDtoMapper;
    }
}
