package jvn.SearchService.controller;

import jvn.SearchService.dto.SearchParamsDTO;
import jvn.SearchService.model.Advertisement;
import jvn.SearchService.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/advertisement", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdvertisementController {

    private AdvertisementService advertisementService;

    @GetMapping
    public ResponseEntity<List<Advertisement>> getAll() {
        return new ResponseEntity<>(advertisementService.getAll(), HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<List<Advertisement>> searchAdvertisements(@Valid @RequestBody SearchParamsDTO searchParamsDTO) {
        List<Advertisement> list = advertisementService.searchAdvertisements(searchParamsDTO);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @Autowired
    public AdvertisementController(AdvertisementService advertisementService) {
        this.advertisementService = advertisementService;
    }
}
