package jvn.Advertisements.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Advertisements.dto.message.Log;
import jvn.Advertisements.dto.request.AdvertisementEditAllInfoDTO;
import jvn.Advertisements.dto.request.AdvertisementEditDTO;
import jvn.Advertisements.dto.request.CreateAdvertisementDTO;
import jvn.Advertisements.dto.request.UserDTO;
import jvn.Advertisements.dto.response.AdvertisementDTO;
import jvn.Advertisements.dto.response.SignedMessageDTO;
import jvn.Advertisements.enumeration.EditType;
import jvn.Advertisements.exceptionHandler.InvalidAdvertisementDataException;
import jvn.Advertisements.mapper.AdvertisementDtoMapper;
import jvn.Advertisements.mapper.AdvertisementEditAllInfoDtoMapper;
import jvn.Advertisements.mapper.CreateAdvertisementDtoMapper;
import jvn.Advertisements.model.Advertisement;
import jvn.Advertisements.producer.LogProducer;
import jvn.Advertisements.service.AdvertisementService;
import jvn.Advertisements.service.DigitalSignatureService;
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

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private AdvertisementService advertisementService;

    private CreateAdvertisementDtoMapper createAdvertisementDtoMapper;

    private AdvertisementDtoMapper advertisementDtoMapper;

    private ObjectMapper objectMapper;

    private HttpServletRequest request;

    private AdvertisementEditAllInfoDtoMapper advertisementEditAllInfoDtoMapper;

    private LogProducer logProducer;

    private DigitalSignatureService digitalSignatureService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdvertisementDTO> create(@Valid @RequestBody CreateAdvertisementDTO createAdvertisementDTO) {
        try {
            UserDTO userDTO = stringToObject(request.getHeader("user"));
            Advertisement advertisement = advertisementService.create(createAdvertisementDtoMapper.toEntity(createAdvertisementDTO), userDTO);
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CAD", String.format("User %s successfully created advertisement %s", userDTO.getId(), advertisement.getId())));
            return new ResponseEntity<>(advertisementDtoMapper.toDto(advertisement), HttpStatus.CREATED);
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
        advertisementService.delete(id, userDTO.getId());
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "DAD", String.format("User %s successfully deleted advertisement %s", userDTO.getId(), id)));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdvertisementDTO> edit(@PathVariable @Positive(message = "Id must be positive.") Long id,
                                                 @Valid @RequestBody AdvertisementEditAllInfoDTO advertisementDTO) {
        try {
            UserDTO userDTO = stringToObject(request.getHeader("user"));
            Advertisement advertisement = advertisementService.edit(id, advertisementEditAllInfoDtoMapper.toEntity(advertisementDTO), userDTO);
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "EAD", String.format("User %s successfully edited advertisement %s", userDTO.getId(), advertisement.getId())));
            return new ResponseEntity<>(advertisementDtoMapper.toDto(advertisement), HttpStatus.OK);
        } catch (ParseException e) {
            throw new InvalidAdvertisementDataException("Please choose valid date.", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/{id}/partial", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdvertisementDTO> editPartial(@PathVariable @Positive(message = "Id must be positive.") Long id,
                                                        @Valid @RequestBody AdvertisementEditDTO advertisementDTO) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        Advertisement advertisement = advertisementService.editPartial(id, advertisementDTO, userDTO.getId());
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "EAD", String.format("User %s successfully edited advertisement %s", userDTO.getId(), advertisement.getId())));
        return new ResponseEntity<>(advertisementDtoMapper.toDto(advertisement), HttpStatus.OK);
    }

    @GetMapping(value = "/car/{carId}/edit-type", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EditType> getCarEditType(@PathVariable("carId") @Positive(message = "Id must be positive.") Long id) {
        return new ResponseEntity<>(advertisementService.getCarEditType(id), HttpStatus.OK);
    }

    @GetMapping(value = "/car/{carId}/edit-type-feign", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SignedMessageDTO> getCarEditTypeFeign(@PathVariable("carId") @Positive(message = "Id must be positive.") Long id) {
        EditType editType = advertisementService.getCarEditType(id);
        byte[] messageBytes = convertToBytes(editType);
        byte[] digitalSignature = digitalSignatureService.encrypt(messageBytes);
        SignedMessageDTO signedMessageDTO = new SignedMessageDTO(messageBytes, digitalSignature);

        return new ResponseEntity<>(signedMessageDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/car/{carId}/check-for-partial-edit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SignedMessageDTO> canEditCarPartially(@PathVariable("carId") @Positive(message = "Id must be positive.") Long carId) {
        Boolean result = advertisementService.canEditCarPartially(carId);
        byte[] messageBytes = convertToBytes(result);
        byte[] digitalSignature = digitalSignatureService.encrypt(messageBytes);
        SignedMessageDTO signedMessageDTO = new SignedMessageDTO(messageBytes, digitalSignature);

        return new ResponseEntity<>(signedMessageDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/car/{carId}/check-for-delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SignedMessageDTO> canDeleteCar(@PathVariable("carId") @Positive(message = "Id must be positive.") Long carId) {
        Boolean result = advertisementService.canDeleteCar(carId);
        byte[] messageBytes = convertToBytes(result);
        byte[] digitalSignature = digitalSignatureService.encrypt(messageBytes);
        SignedMessageDTO signedMessageDTO = new SignedMessageDTO(messageBytes, digitalSignature);

        return new ResponseEntity<>(signedMessageDTO, HttpStatus.OK);
    }

    private UserDTO stringToObject(String user) {
        try {
            return objectMapper.readValue(user, UserDTO.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private byte[] convertToBytes(Boolean obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping %s instance to byte array failed", Boolean.class.getSimpleName())));
            return null;
        }
    }

    private byte[] convertToBytes(EditType obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping %s instance to byte array failed", EditType.class.getSimpleName())));
            return null;
        }
    }

    @Autowired
    public AdvertisementController(AdvertisementService advertisementService, CreateAdvertisementDtoMapper createAdvertisementDtoMapper,
                                   AdvertisementDtoMapper advertisementDtoMapper, ObjectMapper objectMapper, HttpServletRequest request,
                                   AdvertisementEditAllInfoDtoMapper advertisementEditAllInfoDtoMapper, LogProducer logProducer,
                                   DigitalSignatureService digitalSignatureService) {
        this.advertisementService = advertisementService;
        this.createAdvertisementDtoMapper = createAdvertisementDtoMapper;
        this.advertisementDtoMapper = advertisementDtoMapper;
        this.objectMapper = objectMapper;
        this.request = request;
        this.advertisementEditAllInfoDtoMapper = advertisementEditAllInfoDtoMapper;
        this.logProducer = logProducer;
        this.digitalSignatureService = digitalSignatureService;
    }


}
