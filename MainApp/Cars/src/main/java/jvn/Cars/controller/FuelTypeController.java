package jvn.Cars.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Cars.dto.both.FuelTypeDTO;
import jvn.Cars.dto.message.Log;
import jvn.Cars.dto.request.CreateFuelTypeDTO;
import jvn.Cars.dto.request.UserDTO;
import jvn.Cars.mapper.FuelTypeDtoMapper;
import jvn.Cars.model.FuelType;
import jvn.Cars.producer.LogProducer;
import jvn.Cars.service.FuelTypeService;
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
@RequestMapping(value = "/api/fuel-type", produces = MediaType.APPLICATION_JSON_VALUE)
public class FuelTypeController {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private FuelTypeService fuelTypeService;

    private FuelTypeDtoMapper fuelTypeDtoMapper;

    private LogProducer logProducer;

    private HttpServletRequest request;

    private ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FuelTypeDTO> create(@Valid @RequestBody CreateFuelTypeDTO fuelTypeDTO) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));

        FuelType fuelType = fuelTypeService.create(fuelTypeDTO);
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CFT", String.format("User %s successfully created fuel type %s", userDTO.getId(), fuelType.getId())));
        return new ResponseEntity<>(fuelTypeDtoMapper.toDto(fuelType), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FuelTypeDTO>> get() {
        List<FuelTypeDTO> list = fuelTypeService.get().stream().map(fuelTypeDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FuelTypeDTO> edit(@PathVariable @Positive(message = "Id must be positive.") Long id, @Valid @RequestBody FuelTypeDTO fuelTypeDTO) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));

        FuelType fuelType = fuelTypeService.edit(id, fuelTypeDTO);
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "EFT", String.format("User %s successfully edited fuel type %s", userDTO.getId(), fuelType.getId())));
        return new ResponseEntity<>(fuelTypeDtoMapper.toDto(fuelType), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") @Positive(message = "Id must be positive.") Long id) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));

        fuelTypeService.delete(id);
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "DFT", String.format("User %s successfully deleted fuel type %s", userDTO.getId(), id)));
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
    public FuelTypeController(FuelTypeService fuelTypeService, FuelTypeDtoMapper fuelTypeDtoMapper,
                              LogProducer logProducer, HttpServletRequest request, ObjectMapper objectMapper) {
        this.fuelTypeService = fuelTypeService;
        this.fuelTypeDtoMapper = fuelTypeDtoMapper;
        this.logProducer = logProducer;
        this.request = request;
        this.objectMapper = objectMapper;
    }
}
