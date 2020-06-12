package jvn.Advertisements.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Advertisements.dto.request.*;
import jvn.Advertisements.dto.response.AdvertisementDTO;
import jvn.Advertisements.enumeration.EditType;
import jvn.Advertisements.exceptionHandler.InvalidAdvertisementDataException;
import jvn.Advertisements.mapper.AdvertisementDtoMapper;
import jvn.Advertisements.mapper.AdvertisementEditAllInfoDtoMapper;
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
import javax.validation.constraints.Positive;
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

    private AdvertisementEditAllInfoDtoMapper advertisementEditAllInfoDtoMapper;

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

    @GetMapping("/by-ids/{advIds}")
    public ResponseEntity<List<AdvertisementDTO>> getAllMy(@PathVariable("advIds") List<Long> advertisements) {
        List<AdvertisementDTO> list = advertisementService.get(advertisements).stream().map(advertisementDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") @Positive(message = "Id must be positive.") Long id) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        advertisementService.delete(id, userDTO.getId(), request.getHeader("Auth"), request.getHeader("user"));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdvertisementDTO> edit(@PathVariable @Positive(message = "Id must be positive.") Long id,
                                                 @Valid @RequestBody AdvertisementEditAllInfoDTO advertisementDTO) {
        try {
            UserDTO userDTO = stringToObject(request.getHeader("user"));
            return new ResponseEntity<>(advertisementDtoMapper.toDto(advertisementService.edit(id, advertisementEditAllInfoDtoMapper.toEntity(advertisementDTO), userDTO.getId(), request.getHeader("Auth"), request.getHeader("user"), userDTO)), HttpStatus.OK);
        } catch (ParseException e) {
            throw new InvalidAdvertisementDataException("Please choose valid date.", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/{id}/partial", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdvertisementDTO> editPartial(@PathVariable @Positive(message = "Id must be positive.") Long id,
                                                        @Valid @RequestBody AdvertisementEditDTO advertisementDTO) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        return new ResponseEntity<>(advertisementDtoMapper.toDto(advertisementService.editPartial(id, advertisementDTO, userDTO.getId())), HttpStatus.OK);
    }


    @GetMapping(value = "/{id}/location", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LocationDTO> getCarLocation(@PathVariable("id") @Positive(message = "Id must be positive.") Long advId) throws InterruptedException {
        return new ResponseEntity<>(new LocationDTO(advertisementService.getCarLocation(advId)), HttpStatus.OK);
    }

    @GetMapping(value = "/car/{carId}/edit-type", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EditType> getCarEditType(@PathVariable("carId") @Positive(message = "Id must be positive.") Long id) {
        return new ResponseEntity<>(advertisementService.getCarEditType(id), HttpStatus.OK);
    }

    @GetMapping(value = "/car/{carId}/check-for-partial-edit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> canEditCarPartially(@PathVariable("carId") @Positive(message = "Id must be positive.") Long carId) {
        return new ResponseEntity<>(advertisementService.canEditCarPartially(carId, request.getHeader("Auth"), request.getHeader("user")), HttpStatus.OK);
    }

    @GetMapping(value = "/car/{carId}/check-for-delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> canDeleteCar(@PathVariable("carId") @Positive(message = "Id must be positive.") Long carId) {
        return new ResponseEntity<>(advertisementService.canDeleteCar(carId), HttpStatus.OK);
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
                                   AdvertisementDtoMapper advertisementDtoMapper, ObjectMapper objectMapper, HttpServletRequest request,
                                   AdvertisementEditAllInfoDtoMapper advertisementEditAllInfoDtoMapper) {
        this.advertisementService = advertisementService;
        this.createAdvertisementDtoMapper = createAdvertisementDtoMapper;
        this.advertisementDtoMapper = advertisementDtoMapper;
        this.objectMapper = objectMapper;
        this.request = request;
        this.advertisementEditAllInfoDtoMapper = advertisementEditAllInfoDtoMapper;
    }


}
