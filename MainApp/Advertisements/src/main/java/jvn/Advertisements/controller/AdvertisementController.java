package jvn.Advertisements.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Advertisements.dto.request.CreateAdvertisementDTO;
import jvn.Advertisements.dto.request.UserDTO;
import jvn.Advertisements.dto.response.AdvertisementDTO;
import jvn.Advertisements.exceptionHandler.InvalidAdvertisementDataException;
import jvn.Advertisements.mapper.AdvertisementDtoMapper;
import jvn.Advertisements.mapper.CreateAdvertisementDtoMapper;
import jvn.Advertisements.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping(value = "/api/advertisement", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdvertisementController {
    private AdvertisementService advertisementService;

    private CreateAdvertisementDtoMapper createAdvertisementDtoMapper;

    private AdvertisementDtoMapper advertisementDtoMapper;

    private ObjectMapper objectMapper;

    private HttpServletRequest request;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdvertisementDTO> create(@Valid @RequestBody CreateAdvertisementDTO createAdvertisementDTO) {
        try {
            UserDTO userDTO = stringToObject(request.getHeader("user"));
            String jwtToken = request.getHeader("Auth");
            return new ResponseEntity<>(advertisementDtoMapper.toDto(advertisementService.create(createAdvertisementDtoMapper.toEntity(createAdvertisementDTO), userDTO, jwtToken, request.getHeader("user"))),
                    HttpStatus.CREATED);
        } catch (ParseException e) {
            throw new InvalidAdvertisementDataException("Please choose valid date.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{advId}")
    public ResponseEntity<List<AdvertisementDTO>> getAllMy(@PathVariable("advId") List<Long> advertisements) {
        List<AdvertisementDTO> list = advertisementService.get(advertisements).stream().map(advertisementDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
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
    public AdvertisementController(AdvertisementService advertisementService, CreateAdvertisementDtoMapper createAdvertisementDtoMapper,
                                   AdvertisementDtoMapper advertisementDtoMapper, ObjectMapper objectMapper, HttpServletRequest request) {
        this.advertisementService = advertisementService;
        this.createAdvertisementDtoMapper = createAdvertisementDtoMapper;
        this.advertisementDtoMapper = advertisementDtoMapper;
        this.objectMapper = objectMapper;
        this.request = request;
    }


}
