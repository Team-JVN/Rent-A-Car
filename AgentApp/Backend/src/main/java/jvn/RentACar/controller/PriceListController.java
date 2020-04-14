package jvn.RentACar.controller;

import jvn.RentACar.dto.both.PriceListDTO;
import jvn.RentACar.service.PriceListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/price-list", produces = MediaType.APPLICATION_JSON_VALUE)
public class PriceListController {

    private PriceListService priceListService;

    @Autowired
    public PriceListController(PriceListService priceListService) {
        this.priceListService = priceListService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PriceListDTO> get(@PathVariable Long id) {
        return new ResponseEntity<>(priceListService.get(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<PriceListDTO>> getAll() {
        return new ResponseEntity<>(priceListService.getAll(), HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PriceListDTO> create(@Valid @RequestBody PriceListDTO priceListDTO) {
        return new ResponseEntity<>(priceListService.create(priceListDTO), HttpStatus.CREATED);
    }

}
