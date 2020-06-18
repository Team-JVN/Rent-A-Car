package jvn.RentACar.controller;

import jvn.RentACar.dto.both.PriceListDTO;
import jvn.RentACar.mapper.PriceListDtoMapper;
import jvn.RentACar.model.Log;
import jvn.RentACar.model.PriceList;
import jvn.RentACar.service.LogService;
import jvn.RentACar.service.PriceListService;
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
@RequestMapping(value = "/api/price-list", produces = MediaType.APPLICATION_JSON_VALUE)
public class PriceListController {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private PriceListService priceListService;

    private PriceListDtoMapper priceListDtoMapper;

    private LogService logService;

    private UserService userService;

    @GetMapping
    public ResponseEntity<List<PriceListDTO>> getAll() {
        List<PriceListDTO> list = priceListService.getAll().stream().map(priceListDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PriceListDTO> create(@Valid @RequestBody PriceListDTO priceListDTO) {
        PriceList priceList = priceListService.create(priceListDtoMapper.toEntity(priceListDTO));
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CPL", String.format("User %s successfully created price list %s", userService.getLoginUser().getId(), priceList.getId())));
        return new ResponseEntity<>(priceListDtoMapper.toDto(priceList), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PriceListDTO> edit(@PathVariable @Positive(message = "Id must be positive.") Long id,
                                             @Valid @RequestBody PriceListDTO priceListDTO) {
        PriceList priceList = priceListService.edit(id, priceListDtoMapper.toEntity(priceListDTO));
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "EPL", String.format("User %s successfully edited price list %s", userService.getLoginUser().getId(), priceList.getId())));
        return new ResponseEntity<>(priceListDtoMapper.toDto(priceList), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive(message = "Id must be positive.") Long id) {
        priceListService.delete(id);
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "DPL", String.format("User %s successfully deleted price list %s", userService.getLoginUser().getId(), id)));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Autowired
    public PriceListController(PriceListService priceListService, PriceListDtoMapper priceListDtoMapper, LogService logService,
                               UserService userService) {
        this.priceListService = priceListService;
        this.priceListDtoMapper = priceListDtoMapper;
        this.logService = logService;
        this.userService = userService;
    }

}
