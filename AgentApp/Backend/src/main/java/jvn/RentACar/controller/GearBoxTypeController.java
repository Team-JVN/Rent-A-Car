package jvn.RentACar.controller;

import jvn.RentACar.dto.both.GearBoxTypeDTO;
import jvn.RentACar.dto.request.CreateGearBoxTypeDTO;
import jvn.RentACar.service.GearBoxTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/gearbox-type")
public class GearBoxTypeController {

    private GearBoxTypeService gearBoxTypeService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GearBoxTypeDTO> create(@Valid @RequestBody CreateGearBoxTypeDTO createGearBoxTypeDTO) throws Exception {
        return new ResponseEntity<>(gearBoxTypeService.create(createGearBoxTypeDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GearBoxTypeDTO>> get() throws Exception {
        return new ResponseEntity<>(gearBoxTypeService.get(), HttpStatus.OK);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GearBoxTypeDTO> edit(@Valid @RequestBody GearBoxTypeDTO gearBoxTypeDTO) throws Exception {
        return new ResponseEntity<>(gearBoxTypeService.edit(gearBoxTypeDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GearBoxTypeDTO> delete(@PathVariable("id") Long id) throws Exception {
        return new ResponseEntity<>(gearBoxTypeService.delete(id), HttpStatus.ACCEPTED);
    }

    @Autowired
    public GearBoxTypeController(GearBoxTypeService gearBoxTypeService) {
        this.gearBoxTypeService = gearBoxTypeService;
    }
}
