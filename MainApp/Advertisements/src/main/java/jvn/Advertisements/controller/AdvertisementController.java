package jvn.Advertisements.controller;

import jvn.Advertisements.dto.request.CreateAdvertisementDTO;
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

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;
@CrossOrigin
@Validated
@RestController
@RequestMapping(value = "/api/advertisement", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdvertisementController {
    private AdvertisementService advertisementService;

    private CreateAdvertisementDtoMapper createAdvertisementDtoMapper;

    private AdvertisementDtoMapper advertisementDtoMapper;


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdvertisementDTO> create(@Valid @RequestBody CreateAdvertisementDTO createAdvertisementDTO) {
        try {
            return new ResponseEntity<>(advertisementDtoMapper.toDto(advertisementService.create(createAdvertisementDtoMapper.toEntity(createAdvertisementDTO))),
                    HttpStatus.CREATED);
        } catch (ParseException e) {
            throw new InvalidAdvertisementDataException("Please choose valid date.", HttpStatus.BAD_REQUEST);
        }
    }

//    @GetMapping("/all/{status}")
//    public ResponseEntity<List<AdvertisementWithPicturesDTO>> getAll(
//            @PathVariable(value = "status", required = false) @Pattern(regexp = "(?i)(all|active|inactive)$", message = "Status is not valid.") String status) {
//        List<AdvertisementWithPicturesDTO> list = advertisementService.getAll(status).stream().map(adWithPicturesDtoMapper::toDto).
//                collect(Collectors.toList());
//        return new ResponseEntity<>(list, HttpStatus.OK);
//    }

    @Autowired
    public AdvertisementController(AdvertisementService advertisementService, CreateAdvertisementDtoMapper createAdvertisementDtoMapper,
                                   AdvertisementDtoMapper advertisementDtoMapper) {
        this.advertisementService = advertisementService;
        this.createAdvertisementDtoMapper = createAdvertisementDtoMapper;
        this.advertisementDtoMapper = advertisementDtoMapper;
    }
}
