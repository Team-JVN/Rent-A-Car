package jvn.RentACar.controller;

import jvn.RentACar.dto.response.AdvertisementDTO;
import jvn.RentACar.dto.both.RentRequestDTO;
import jvn.RentACar.dto.request.AdvertisementEditDTO;
import jvn.RentACar.dto.request.CreateAdvertisementDTO;
import jvn.RentACar.dto.response.AdvertisementWithPicturesDTO;
import jvn.RentACar.enumeration.EditType;
import jvn.RentACar.exceptionHandler.InvalidAdvertisementDataException;
import jvn.RentACar.mapper.AdvertisementDtoMapper;
import jvn.RentACar.mapper.AdvertisementWithPicturesDtoMapper;
import jvn.RentACar.mapper.CreateAdvertisementDtoMapper;
import jvn.RentACar.mapper.RentRequestDtoMapper;
import jvn.RentACar.service.AdvertisementService;
import jvn.RentACar.service.RentRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
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

    private AdvertisementWithPicturesDtoMapper adWithPicturesDtoMapper;

    private RentRequestService rentRequestService;

    private RentRequestDtoMapper rentRequestDtoMapper;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<AdvertisementDTO> create(@Valid @RequestBody CreateAdvertisementDTO createAdvertisementDTO) {
        try {
            return new ResponseEntity<>(advertisementDtoMapper.toDto(advertisementService.create(createAdvertisementDtoMapper.toEntity(createAdvertisementDTO))),
                    HttpStatus.CREATED);
        } catch (ParseException e) {
            throw new InvalidAdvertisementDataException("Please choose valid date.", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<AdvertisementDTO> edit(@PathVariable @Positive(message = "Id must be positive.") Long id, @Valid @RequestBody AdvertisementWithPicturesDTO advertisementDTO) {
        try {
            return new ResponseEntity<>(advertisementDtoMapper.toDto(advertisementService.edit(id, adWithPicturesDtoMapper.toEntity(advertisementDTO))), HttpStatus.OK);
        } catch (ParseException e) {
            throw new InvalidAdvertisementDataException("Please choose valid date.", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/{id}/partial", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<AdvertisementDTO> editPartial(@PathVariable @Positive(message = "Id must be positive.") Long id, @Valid @RequestBody AdvertisementEditDTO advertisementDTO) {
        return new ResponseEntity<>(advertisementDtoMapper.toDto(advertisementService.editPartial(id, advertisementDTO)), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<EditType> getEditType(@PathVariable @Positive(message = "Id must be positive.") Long id) {
        return new ResponseEntity<>(advertisementService.getEditType(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<Void> delete(@PathVariable("id") @Positive(message = "Id must be positive.") Long id) {
        advertisementService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/all/{status}")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<List<AdvertisementWithPicturesDTO>> getAll(
            @PathVariable(value = "status", required = false) @Pattern(regexp = "(?i)(all|active|inactive)$",message = "Status is not valid.") String status) {
        List<AdvertisementWithPicturesDTO> list = advertisementService.getAll(status).stream().map(adWithPicturesDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/{advertisementId}/rent-requests/{status}")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<List<RentRequestDTO>> getRentRequests(@PathVariable(value = "advertisementId", required = false)  @Positive(message = "Id must be positive.") Long advertisementId,
                                                                @PathVariable(value = "status", required = false) @Pattern(regexp = "(?i)(all|pending|reserved|paid|canceled)$",message = "Status is not valid.")
                                                                        String status) {
        List<RentRequestDTO> list = rentRequestService.get(advertisementId, status).stream().map(rentRequestDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdvertisementWithPicturesDTO> get(@PathVariable @Positive(message = "Id must be positive.") Long id) {
        return new ResponseEntity<>(adWithPicturesDtoMapper.toDto(advertisementService.get(id)), HttpStatus.OK);
    }

    @Autowired
    public AdvertisementController(AdvertisementService advertisementService, CreateAdvertisementDtoMapper createAdvertisementDtoMapper,
                                   AdvertisementDtoMapper advertisementDtoMapper, AdvertisementWithPicturesDtoMapper adWithPicturesDtoMapper,
                                   RentRequestDtoMapper rentRequestDtoMapper, RentRequestService rentRequestService) {
        this.advertisementService = advertisementService;
        this.createAdvertisementDtoMapper = createAdvertisementDtoMapper;
        this.advertisementDtoMapper = advertisementDtoMapper;
        this.adWithPicturesDtoMapper = adWithPicturesDtoMapper;
        this.rentRequestDtoMapper = rentRequestDtoMapper;
        this.rentRequestService = rentRequestService;
    }
}
