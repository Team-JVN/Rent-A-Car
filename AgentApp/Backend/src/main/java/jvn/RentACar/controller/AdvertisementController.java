package jvn.RentACar.controller;

import jvn.RentACar.dto.both.AdvertisementDTO;
import jvn.RentACar.dto.request.CreateAdvertisementDTO;
import jvn.RentACar.dto.response.AdvertisementWithPicturesDTO;
import jvn.RentACar.exceptionHandler.InvalidAdvertisementDataException;
import jvn.RentACar.mapper.AdvertisementDtoMapper;
import jvn.RentACar.mapper.AdvertisementWithPicturesDtoMapper;
import jvn.RentACar.mapper.CreateAdvertisementDtoMapper;
import jvn.RentACar.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping(value = "/api/advertisement", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdvertisementController {

    private AdvertisementService advertisementService;

    private CreateAdvertisementDtoMapper createAdvertisementDtoMapper;

    private AdvertisementDtoMapper advertisementDtoMapper;
    private AdvertisementWithPicturesDtoMapper adWithPicturesDtoMapper;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdvertisementDTO> create(@Valid @RequestBody CreateAdvertisementDTO createAdvertisementDTO) {
        try {
            return new ResponseEntity<>(advertisementDtoMapper.toDto(advertisementService.create(createAdvertisementDtoMapper.toEntity(createAdvertisementDTO))),
                    HttpStatus.CREATED);
        } catch (ParseException e) {
            throw new InvalidAdvertisementDataException("Please choose valid date.", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdvertisementDTO> edit(@PathVariable Long id, @Valid @RequestBody AdvertisementDTO advertisementDTO) {
        try {
            return new ResponseEntity<>(advertisementDtoMapper.toDto(advertisementService.edit(id, advertisementDtoMapper.toEntity(advertisementDTO))), HttpStatus.OK);
        } catch (ParseException e) {
            throw new InvalidAdvertisementDataException("Please choose valid date.", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        advertisementService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<AdvertisementWithPicturesDTO>> getAll() {
        return new ResponseEntity<>(advertisementService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdvertisementWithPicturesDTO> get(@PathVariable Long id) {
        return new ResponseEntity<>(adWithPicturesDtoMapper.toDto(advertisementService.get(id)), HttpStatus.OK);
    }

    @Autowired
    public AdvertisementController(AdvertisementService advertisementService, CreateAdvertisementDtoMapper createAdvertisementDtoMapper,
                                   AdvertisementDtoMapper advertisementDtoMapper, AdvertisementWithPicturesDtoMapper adWithPicturesDtoMapper) {
        this.advertisementService = advertisementService;
        this.createAdvertisementDtoMapper = createAdvertisementDtoMapper;
        this.advertisementDtoMapper = advertisementDtoMapper;
        this.adWithPicturesDtoMapper = adWithPicturesDtoMapper;
    }
}
