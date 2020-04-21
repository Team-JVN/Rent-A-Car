package jvn.RentACar.controller;

import jvn.RentACar.dto.both.RentRequestDTO;
import jvn.RentACar.exceptionHandler.InvalidAdvertisementDataException;
import jvn.RentACar.mapper.RentRequestDtoMapper;
import jvn.RentACar.service.RentRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping(value = "/api/rent-request", produces = MediaType.APPLICATION_JSON_VALUE)
public class RentRequestController {

    private RentRequestService rentRequestService;

    private RentRequestDtoMapper rentRequestDtoMapper;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RentRequestDTO> create(@Valid @RequestBody RentRequestDTO rentRequestDTO) {
        try {
            return new ResponseEntity<>(rentRequestDtoMapper.toDto(rentRequestService.create(rentRequestDtoMapper.toEntity(rentRequestDTO))),
                    HttpStatus.CREATED);
        } catch (DateTimeParseException e) {
            throw new InvalidAdvertisementDataException("Please choose valid date and time.", HttpStatus.BAD_REQUEST);
        }
    }

    @Autowired
    public RentRequestController(RentRequestService rentRequestService, RentRequestDtoMapper rentRequestDtoMapper) {
        this.rentRequestService = rentRequestService;
        this.rentRequestDtoMapper = rentRequestDtoMapper;
    }
}
