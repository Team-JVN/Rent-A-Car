package jvn.RentACar.controller;

import jvn.RentACar.dto.both.BodyStyleDTO;
import jvn.RentACar.dto.request.CreateBodyStyleDTO;
import jvn.RentACar.mapper.BodyStyleDtoMapper;
import jvn.RentACar.service.BodyStyleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/body-style", produces = MediaType.APPLICATION_JSON_VALUE)
public class BodyStyleController {

    private BodyStyleService bodyStyleService;

    private BodyStyleDtoMapper bodyStyleDtoMapper;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BodyStyleDTO> create(@Valid @RequestBody CreateBodyStyleDTO bodyStyleDTO) {
        return new ResponseEntity<>(bodyStyleDtoMapper.toDto(bodyStyleService.create(bodyStyleDTO)), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BodyStyleDTO>> get() {
        List<BodyStyleDTO> list = bodyStyleService.get().stream().map(bodyStyleDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BodyStyleDTO> edit(@PathVariable Long id, @Valid @RequestBody BodyStyleDTO bodyStyleDTO) {
        return new ResponseEntity<>(bodyStyleDtoMapper.toDto(bodyStyleService.edit(id, bodyStyleDTO)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        bodyStyleService.delete(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @Autowired
    public BodyStyleController(BodyStyleService bodyStyleService, BodyStyleDtoMapper bodyStyleDtoMapper) {
        this.bodyStyleService = bodyStyleService;
        this.bodyStyleDtoMapper = bodyStyleDtoMapper;
    }
}
