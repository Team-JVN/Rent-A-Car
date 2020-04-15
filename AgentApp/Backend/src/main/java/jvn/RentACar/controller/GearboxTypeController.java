package jvn.RentACar.controller;

import jvn.RentACar.dto.both.GearboxTypeDTO;
import jvn.RentACar.dto.request.CreateGearboxTypeDTO;
import jvn.RentACar.service.GearboxTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/gearbox-type", produces = MediaType.APPLICATION_JSON_VALUE)
public class GearboxTypeController {

    private GearboxTypeService gearBoxTypeService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GearboxTypeDTO> create(@Valid @RequestBody CreateGearboxTypeDTO createGearBoxTypeDTO) {
        return new ResponseEntity<>(gearBoxTypeService.create(createGearBoxTypeDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GearboxTypeDTO>> get() {
        return new ResponseEntity<>(gearBoxTypeService.get(), HttpStatus.OK);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GearboxTypeDTO> edit(@Valid @RequestBody GearboxTypeDTO gearBoxTypeDTO) {
        return new ResponseEntity<>(gearBoxTypeService.edit(gearBoxTypeDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GearboxTypeDTO> delete(@PathVariable("id") Long id) {
        return new ResponseEntity<>(gearBoxTypeService.delete(id), HttpStatus.ACCEPTED);
    }

    @Autowired
    public GearboxTypeController(GearboxTypeService gearBoxTypeService) {
        this.gearBoxTypeService = gearBoxTypeService;
    }
}
