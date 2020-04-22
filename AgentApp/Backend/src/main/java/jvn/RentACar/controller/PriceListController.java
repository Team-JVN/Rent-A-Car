package jvn.RentACar.controller;

import jvn.RentACar.dto.both.PriceListDTO;
import jvn.RentACar.mapper.PriceListDtoMapper;
import jvn.RentACar.service.PriceListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/price-list", produces = MediaType.APPLICATION_JSON_VALUE)
public class PriceListController {

    private PriceListService priceListService;

    private PriceListDtoMapper priceListDtoMapper;

    @GetMapping("/{id}")
    public ResponseEntity<PriceListDTO> get(@PathVariable Long id) {
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
    public ResponseEntity<PriceListDTO> edit(@PathVariable Long id, @Valid @RequestBody PriceListDTO priceListDTO) {
        return new ResponseEntity<>(priceListDtoMapper.toDto(priceListService.edit(id, priceListDtoMapper.toEntity(priceListDTO))), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        priceListService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Autowired
    public PriceListController(PriceListService priceListService, PriceListDtoMapper priceListDtoMapper) {
        this.priceListService = priceListService;
        this.priceListDtoMapper = priceListDtoMapper;
    }

}
