package jvn.RentACar.controller;

import jvn.RentACar.dto.both.FuelTypeDTO;
import jvn.RentACar.dto.request.CreateFuelTypeDTO;
import jvn.RentACar.mapper.FuelTypeDtoMapper;
import jvn.RentACar.service.FuelTypeService;
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

    private FuelTypeService fuelTypeService;

    private FuelTypeDtoMapper fuelTypeDtoMapper;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FuelTypeDTO> create(@Valid @RequestBody CreateFuelTypeDTO fuelTypeDTO) {
        return new ResponseEntity<>(fuelTypeDtoMapper.toDto(fuelTypeService.create(fuelTypeDTO)), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FuelTypeDTO>> get() {
        List<FuelTypeDTO> list = fuelTypeService.get().stream().map(fuelTypeDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FuelTypeDTO> edit(@PathVariable @Positive(message = "Id must be positive.") Long id, @Valid @RequestBody FuelTypeDTO fuelTypeDTO) {
        return new ResponseEntity<>(fuelTypeDtoMapper.toDto(fuelTypeService.edit(id, fuelTypeDTO)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") @Positive(message = "Id must be positive.") Long id) {
        fuelTypeService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Autowired
    public FuelTypeController(FuelTypeService fuelTypeService, FuelTypeDtoMapper fuelTypeDtoMapper) {
        this.fuelTypeService = fuelTypeService;
        this.fuelTypeDtoMapper = fuelTypeDtoMapper;
    }
}
