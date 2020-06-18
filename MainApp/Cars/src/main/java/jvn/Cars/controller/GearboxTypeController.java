package jvn.Cars.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Cars.dto.both.GearboxTypeDTO;
import jvn.Cars.dto.message.Log;
import jvn.Cars.dto.request.CreateGearboxTypeDTO;
import jvn.Cars.dto.request.UserDTO;
import jvn.Cars.mapper.GearboxTypeDtoMapper;
import jvn.Cars.model.GearboxType;
import jvn.Cars.producer.LogProducer;
import jvn.Cars.service.GearboxTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping(value = "/api/gearbox-type", produces = MediaType.APPLICATION_JSON_VALUE)
public class GearboxTypeController {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private GearboxTypeService gearBoxTypeService;

    private GearboxTypeDtoMapper gearboxTypeDtoMapper;

    private LogProducer logProducer;

    private HttpServletRequest request;

    private ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GearboxTypeDTO> create(@Valid @RequestBody CreateGearboxTypeDTO createGearBoxTypeDTO) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));

        GearboxType gearboxType = gearBoxTypeService.create(createGearBoxTypeDTO);
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CGT", String.format("User %s successfully created gearbox type %s", userDTO.getId(), gearboxType.getId())));
        return new ResponseEntity<>(gearboxTypeDtoMapper.toDto(gearboxType), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GearboxTypeDTO>> get() {
        List<GearboxTypeDTO> list = gearBoxTypeService.get().stream().map(gearboxTypeDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GearboxTypeDTO> edit(@PathVariable @Positive(message = "Id must be positive.") Long id, @Valid @RequestBody GearboxTypeDTO gearBoxTypeDTO) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));

        GearboxType gearboxType = gearBoxTypeService.edit(id, gearBoxTypeDTO);
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "EGT", String.format("User %s successfully edited gearbox type %s", userDTO.getId(), gearboxType.getId())));
        return new ResponseEntity<>(gearboxTypeDtoMapper.toDto(gearboxType), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") @Positive(message = "Id must be positive.") Long id) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));

        gearBoxTypeService.delete(id);
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "DGT", String.format("User %s successfully deleted gearbox type %s", userDTO.getId(), id)));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private UserDTO stringToObject(String user) {
        try {
            return objectMapper.readValue(user, UserDTO.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Autowired
    public GearboxTypeController(GearboxTypeService gearBoxTypeService, GearboxTypeDtoMapper gearboxTypeDtoMapper,
                                 LogProducer logProducer, HttpServletRequest request, ObjectMapper objectMapper) {
        this.gearBoxTypeService = gearBoxTypeService;
        this.gearboxTypeDtoMapper = gearboxTypeDtoMapper;
        this.logProducer = logProducer;
        this.request = request;
        this.objectMapper = objectMapper;
    }
}
