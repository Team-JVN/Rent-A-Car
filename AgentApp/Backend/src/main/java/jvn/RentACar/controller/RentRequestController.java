package jvn.RentACar.controller;

import jvn.RentACar.dto.both.RentRequestDTO;
import jvn.RentACar.exceptionHandler.InvalidAdvertisementDataException;
import jvn.RentACar.mapper.RentRequestDtoMapper;
import jvn.RentACar.service.RentRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/rent-request", produces = MediaType.APPLICATION_JSON_VALUE)
public class RentRequestController {

    private RentRequestService rentRequestService;

    private RentRequestDtoMapper rentRequestDtoMapper;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<RentRequestDTO> create(@Valid @RequestBody RentRequestDTO rentRequestDTO) {
        try {
            return new ResponseEntity<>(rentRequestDtoMapper.toDto(rentRequestService.create(rentRequestDtoMapper.toEntity(rentRequestDTO))),
                    HttpStatus.CREATED);
        } catch (DateTimeParseException e) {
            throw new InvalidAdvertisementDataException("Please choose valid date and time.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{status}/mine")
    public ResponseEntity<List<RentRequestDTO>> getMine(@PathVariable(value = "status") String status) {
        List<RentRequestDTO> list = rentRequestService.getMine(status).stream().map(rentRequestDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentRequestDTO> get(@PathVariable Long id) {
        return new ResponseEntity<>(rentRequestDtoMapper.toDto(rentRequestService.get(id)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rentRequestService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @Autowired
    public RentRequestController(RentRequestService rentRequestService, RentRequestDtoMapper rentRequestDtoMapper) {
        this.rentRequestService = rentRequestService;
        this.rentRequestDtoMapper = rentRequestDtoMapper;
    }
}
