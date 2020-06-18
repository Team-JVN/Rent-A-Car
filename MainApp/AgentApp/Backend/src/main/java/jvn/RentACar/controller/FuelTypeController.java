package jvn.RentACar.controller;

import jvn.RentACar.dto.both.FuelTypeDTO;
import jvn.RentACar.dto.request.CreateFuelTypeDTO;
import jvn.RentACar.mapper.FuelTypeDtoMapper;
import jvn.RentACar.model.FuelType;
import jvn.RentACar.model.Log;
import jvn.RentACar.service.FuelTypeService;
import jvn.RentACar.service.LogService;
import jvn.RentACar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    private LogService logService;

    private UserService userService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FuelTypeDTO> create(@Valid @RequestBody CreateFuelTypeDTO fuelTypeDTO) {
        FuelType fuelType = fuelTypeService.create(fuelTypeDTO);
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CFT", String.format("User %s successfully created fuel type %s", userService.getLoginUser().getId(), fuelType.getId())));
        return new ResponseEntity<>(fuelTypeDtoMapper.toDto(fuelType), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FuelTypeDTO>> get() {
        List<FuelTypeDTO> list = fuelTypeService.get().stream().map(fuelTypeDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FuelTypeDTO> edit(@PathVariable @Positive(message = "Id must be positive.") Long id,
                                            @Valid @RequestBody FuelTypeDTO fuelTypeDTO) {
        FuelType fuelType = fuelTypeService.edit(id, fuelTypeDTO);
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "EFT", String.format("User %s successfully edited fuel type %s", userService.getLoginUser().getId(), fuelType.getId())));
        return new ResponseEntity<>(fuelTypeDtoMapper.toDto(fuelType), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") @Positive(message = "Id must be positive.") Long id) {
        fuelTypeService.delete(id);
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "DFT", String.format("User %s successfully deleted fuel type %s", userService.getLoginUser().getId(), id)));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Autowired
    public FuelTypeController(FuelTypeService fuelTypeService, FuelTypeDtoMapper fuelTypeDtoMapper, LogService logService,
                              UserService userService) {
        this.fuelTypeService = fuelTypeService;
        this.fuelTypeDtoMapper = fuelTypeDtoMapper;
        this.logService = logService;
        this.userService = userService;
    }
}
