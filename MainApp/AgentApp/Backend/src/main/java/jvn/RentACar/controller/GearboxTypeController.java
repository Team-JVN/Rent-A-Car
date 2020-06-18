package jvn.RentACar.controller;

import jvn.RentACar.dto.both.GearboxTypeDTO;
import jvn.RentACar.dto.request.CreateGearboxTypeDTO;
import jvn.RentACar.mapper.GearboxTypeDtoMapper;
import jvn.RentACar.model.GearboxType;
import jvn.RentACar.model.Log;
import jvn.RentACar.service.GearboxTypeService;
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
@RequestMapping(value = "/api/gearbox-type", produces = MediaType.APPLICATION_JSON_VALUE)
public class GearboxTypeController {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private GearboxTypeService gearBoxTypeService;

    private GearboxTypeDtoMapper gearboxTypeDtoMapper;

    private LogService logService;

    private UserService userService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GearboxTypeDTO> create(@Valid @RequestBody CreateGearboxTypeDTO createGearBoxTypeDTO) {
        GearboxType gearboxType = gearBoxTypeService.create(createGearBoxTypeDTO);
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CGT", String.format("User %s successfully created gearbox type %s", userService.getLoginUser().getId(), gearboxType.getId())));
        return new ResponseEntity<>(gearboxTypeDtoMapper.toDto(gearboxType), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GearboxTypeDTO>> get() {
        List<GearboxTypeDTO> list = gearBoxTypeService.get().stream().map(gearboxTypeDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GearboxTypeDTO> edit(@PathVariable @Positive(message = "Id must be positive.") Long id,
                                               @Valid @RequestBody GearboxTypeDTO gearBoxTypeDTO) {
        GearboxType gearboxType = gearBoxTypeService.edit(id, gearBoxTypeDTO);
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "EGT", String.format("User %s successfully edited gearbox type %s", userService.getLoginUser().getId(), gearboxType.getId())));
        return new ResponseEntity<>(gearboxTypeDtoMapper.toDto(gearBoxTypeService.edit(id, gearBoxTypeDTO)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") @Positive(message = "Id must be positive.") Long id) {
        gearBoxTypeService.delete(id);
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "DGT", String.format("User %s successfully deleted gearbox type %s", userService.getLoginUser().getId(), id)));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Autowired
    public GearboxTypeController(GearboxTypeService gearBoxTypeService, GearboxTypeDtoMapper gearboxTypeDtoMapper, LogService logService,
                                 UserService userService) {
        this.gearBoxTypeService = gearBoxTypeService;
        this.gearboxTypeDtoMapper = gearboxTypeDtoMapper;
        this.logService = logService;
        this.userService = userService;
    }
}
