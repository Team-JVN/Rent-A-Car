package jvn.RentACar.controller;

import jvn.RentACar.dto.BodyStyleDTO;
import jvn.RentACar.dto.CreateBodyStyleDTO;
import jvn.RentACar.service.BodyStyleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/body-style")
public class BodyStyleController {

    @Autowired
    private BodyStyleService bodyStyleService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BodyStyleDTO> create(@Valid @RequestBody CreateBodyStyleDTO bodyStyleDTO) throws Exception {
        return new ResponseEntity<>(bodyStyleService.create(bodyStyleDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BodyStyleDTO>> get() throws Exception {
        return new ResponseEntity<>(bodyStyleService.get(), HttpStatus.OK);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BodyStyleDTO> edit(@Valid @RequestBody BodyStyleDTO bodyStyleDTO) throws Exception {
        return new ResponseEntity<>(bodyStyleService.edit(bodyStyleDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BodyStyleDTO> delete(@PathVariable("id") Long id) throws Exception {
        return new ResponseEntity<>(bodyStyleService.delete(id), HttpStatus.ACCEPTED);
    }
}
