package jvn.RentACar.controller;

import jvn.RentACar.dto.both.GearboxTypeDTO;
import jvn.RentACar.dto.request.CreateGearboxTypeDTO;
import jvn.RentACar.mapper.GearboxTypeDtoMapper;
import jvn.RentACar.service.GearboxTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/gearbox-type", produces = MediaType.APPLICATION_JSON_VALUE)
public class GearboxTypeController {

    private GearboxTypeService gearBoxTypeService;

    private GearboxTypeDtoMapper gearboxTypeDtoMapper;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<GearboxTypeDTO> create(@Valid @RequestBody CreateGearboxTypeDTO createGearBoxTypeDTO) {
        return new ResponseEntity<>(gearboxTypeDtoMapper.toDto(gearBoxTypeService.create(createGearBoxTypeDTO)), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GearboxTypeDTO>> get() {
        List<GearboxTypeDTO> list = gearBoxTypeService.get().stream().map(gearboxTypeDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<GearboxTypeDTO> edit(@PathVariable Long id, @Valid @RequestBody GearboxTypeDTO gearBoxTypeDTO) {
        return new ResponseEntity<>(gearboxTypeDtoMapper.toDto(gearBoxTypeService.edit(id, gearBoxTypeDTO)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        gearBoxTypeService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Autowired
    public GearboxTypeController(GearboxTypeService gearBoxTypeService, GearboxTypeDtoMapper gearboxTypeDtoMapper) {
        this.gearBoxTypeService = gearBoxTypeService;
        this.gearboxTypeDtoMapper = gearboxTypeDtoMapper;
    }
}
