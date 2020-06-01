package jvn.Renting.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Renting.dto.both.RentRequestDTO;
import jvn.Renting.dto.both.UserDTO;
import jvn.Renting.exceptionHandler.InvalidRentRequestDataException;
import jvn.Renting.mapper.RentRequestDtoMapper;
import jvn.Renting.service.RentRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.ParseException;
import java.time.format.DateTimeParseException;

@Validated
@RestController
@RequestMapping(value = "/api/rent-request", produces = MediaType.APPLICATION_JSON_VALUE)
public class RentRequestController {

    private RentRequestService rentRequestService;

    private RentRequestDtoMapper rentRequestDtoMapper;

    private ObjectMapper objectMapper;

    private HttpServletRequest request;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RentRequestDTO> create(@Valid @RequestBody RentRequestDTO rentRequestDTO) {
        try {
            UserDTO userDTO = stringToObject(request.getHeader("user"));
            return new ResponseEntity<>(rentRequestDtoMapper.toDto(rentRequestService.create(rentRequestDtoMapper.toEntity(rentRequestDTO), userDTO)),
                    HttpStatus.CREATED);
        } catch (DateTimeParseException | ParseException e) {
            throw new InvalidRentRequestDataException("Please choose valid date and time.", HttpStatus.BAD_REQUEST);
        }
    }

    private UserDTO stringToObject(String user) {
        try {
            return objectMapper.readValue(user, UserDTO.class);
        } catch (JsonProcessingException e) {
            //TODO: Add to log and delete return null;
            return null;
        }
    }

    @Autowired
    public RentRequestController(RentRequestService rentRequestService, RentRequestDtoMapper rentRequestDtoMapper,
                                 ObjectMapper objectMapper, HttpServletRequest request) {
        this.rentRequestService = rentRequestService;
        this.rentRequestDtoMapper = rentRequestDtoMapper;
        this.objectMapper = objectMapper;
        this.request = request;
    }
}
