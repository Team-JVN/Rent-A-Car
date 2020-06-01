package jvn.Advertisements.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Advertisements.dto.both.PriceListDTO;
import jvn.Advertisements.dto.request.UserDTO;
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

@Validated
@RestController
@RequestMapping(value = "/api/price-list", produces = MediaType.APPLICATION_JSON_VALUE)
public class PriceListController {

    private PriceListService priceListService;

    private PriceListDtoMapper priceListDtoMapper;

    private HttpServletRequest request;

    private ObjectMapper objectMapper;

    @GetMapping("/{id}")
    public ResponseEntity<PriceListDTO> get(@PathVariable @Positive(message = "Id must be positive.") Long id) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        return new ResponseEntity<>(priceListDtoMapper.toDto(priceListService.get(id,userDTO)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<PriceListDTO>> getAll() {
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        List<PriceListDTO> list = priceListService.getAll(userDTO).stream().map(priceListDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PriceListDTO> create(@Valid @RequestBody PriceListDTO priceListDTO) {
        UserDTO userDTO = stringToObject( request.getHeader("user"));
        return new ResponseEntity<>(priceListDtoMapper.toDto(priceListService.create(priceListDtoMapper.toEntity(priceListDTO),userDTO)), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PriceListDTO> edit(@PathVariable @Positive(message = "Id must be positive.") Long id, @Valid @RequestBody PriceListDTO priceListDTO) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        return new ResponseEntity<>(priceListDtoMapper.toDto(priceListService.edit(id, priceListDtoMapper.toEntity(priceListDTO),userDTO)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive(message = "Id must be positive.") Long id) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        priceListService.delete(id,userDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private UserDTO stringToObject(String user) {
        try {
            return objectMapper.readValue(user, UserDTO.class);
        } catch (JsonProcessingException e) {
            //TODO: Add to log and delete return null;
            return null;
        }
    }

    @Autowired
    public PriceListController(PriceListService priceListService, PriceListDtoMapper priceListDtoMapper,HttpServletRequest request,
                               ObjectMapper objectMapper) {
        this.priceListService = priceListService;
        this.priceListDtoMapper = priceListDtoMapper;
        this.request = request;
        this.objectMapper = objectMapper;
    }


}
