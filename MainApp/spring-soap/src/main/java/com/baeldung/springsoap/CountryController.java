package com.baeldung.springsoap;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class CountryController {

    @GetMapping
    public ResponseEntity<CDTO> getAll() {
        CDTO cdto = new CDTO();
        cdto.setName("pera");
        return new ResponseEntity<>(cdto, HttpStatus.OK);
    }
}
