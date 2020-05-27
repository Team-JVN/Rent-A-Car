package jvn.Advertisements.controller;

import jvn.Advertisements.dto.both.PriceListDTO;
import jvn.Advertisements.mapper.PriceListDtoMapper;
import jvn.Advertisements.service.PriceListService;
import org.apache.commons.fileupload.RequestContext;
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
@CrossOrigin
@Validated
@RestController
@RequestMapping(value = "/api/price-list", produces = MediaType.APPLICATION_JSON_VALUE)
public class PriceListController {

    private PriceListService priceListService;

    private PriceListDtoMapper priceListDtoMapper;

    private HttpServletRequest request;

    @GetMapping("/{id}")
    public ResponseEntity<PriceListDTO> get(@PathVariable @Positive(message = "Id must be positive.") Long id) {

        return new ResponseEntity<>(priceListDtoMapper.toDto(priceListService.get(id)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<PriceListDTO>> getAll() {
        List<PriceListDTO> list = priceListService.getAll().stream().map(priceListDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PriceListDTO> create(@Valid @RequestBody PriceListDTO priceListDTO) {
        return new ResponseEntity<>(priceListDtoMapper.toDto(priceListService.create(priceListDtoMapper.toEntity(priceListDTO))), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PriceListDTO> edit(@PathVariable @Positive(message = "Id must be positive.") Long id, @Valid @RequestBody PriceListDTO priceListDTO) {
        return new ResponseEntity<>(priceListDtoMapper.toDto(priceListService.edit(id, priceListDtoMapper.toEntity(priceListDTO))), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive(message = "Id must be positive.") Long id) {
        priceListService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Autowired
    public PriceListController(PriceListService priceListService, PriceListDtoMapper priceListDtoMapper,HttpServletRequest request) {
        this.priceListService = priceListService;
        this.priceListDtoMapper = priceListDtoMapper;
        this.request = request;
    }


}
