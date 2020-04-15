package jvn.RentACar.controller;

import jvn.RentACar.dto.both.FuelTypeDTO;
import jvn.RentACar.dto.request.CreateFuelTypeDTO;
import jvn.RentACar.service.FuelTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/fuel-type", produces = MediaType.APPLICATION_JSON_VALUE)
public class FuelTypeController {

    private FuelTypeService fuelTypeService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FuelTypeDTO> create(@Valid @RequestBody CreateFuelTypeDTO fuelTypeDTO) {
        return new ResponseEntity<>(fuelTypeService.create(fuelTypeDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FuelTypeDTO>> get() {
        return new ResponseEntity<>(fuelTypeService.get(), HttpStatus.OK);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FuelTypeDTO> edit(@Valid @RequestBody FuelTypeDTO fuelTypeDTO) {
        return new ResponseEntity<>(fuelTypeService.edit(fuelTypeDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FuelTypeDTO> delete(@PathVariable("id") Long id) {
        return new ResponseEntity<>(fuelTypeService.delete(id), HttpStatus.ACCEPTED);
    }

    @Autowired
    public FuelTypeController(FuelTypeService fuelTypeService) {
        this.fuelTypeService = fuelTypeService;
    }
}
