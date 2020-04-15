package jvn.RentACar.controller;

import jvn.RentACar.dto.both.BodyStyleDTO;
import jvn.RentACar.dto.request.CreateBodyStyleDTO;
import jvn.RentACar.model.BodyStyle;
import jvn.RentACar.service.BodyStyleService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/body-style", produces = MediaType.APPLICATION_JSON_VALUE)
public class BodyStyleController {

    private BodyStyleService bodyStyleService;

    private ModelMapper modelMapper;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BodyStyleDTO> create(@Valid @RequestBody CreateBodyStyleDTO bodyStyleDTO) {
        return new ResponseEntity<>(convertToDto(bodyStyleService.create(bodyStyleDTO)), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BodyStyleDTO>> get() {
        return new ResponseEntity<>(bodyStyleService.get(), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BodyStyleDTO> edit(@PathVariable Long id, @Valid @RequestBody BodyStyleDTO bodyStyleDTO) {
        return new ResponseEntity<>(bodyStyleService.edit(id, bodyStyleDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BodyStyleDTO> delete(@PathVariable("id") Long id) {
        return new ResponseEntity<>(bodyStyleService.delete(id), HttpStatus.ACCEPTED);
    }


    private BodyStyleDTO convertToDto(BodyStyle bodyStyle) {
        BodyStyleDTO bodyStyleDTO = modelMapper.map(bodyStyle, BodyStyleDTO.class);
        return bodyStyleDTO;
    }

    @Autowired
    public BodyStyleController(BodyStyleService bodyStyleService, ModelMapper modelMapper) {
        this.bodyStyleService = bodyStyleService;
        this.modelMapper = modelMapper;
    }
}
