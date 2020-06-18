package jvn.Advertisements.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Advertisements.dto.both.PriceListDTO;
import jvn.Advertisements.dto.message.Log;
import jvn.Advertisements.dto.request.UserDTO;
import jvn.Advertisements.mapper.PriceListDtoMapper;
import jvn.Advertisements.model.PriceList;
import jvn.Advertisements.producer.LogProducer;
import jvn.Advertisements.service.PriceListService;
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

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private PriceListService priceListService;

    private PriceListDtoMapper priceListDtoMapper;

    private HttpServletRequest request;

    private ObjectMapper objectMapper;

    private LogProducer logProducer;

    @GetMapping("/{id}")
    public ResponseEntity<PriceListDTO> get(@PathVariable @Positive(message = "Id must be positive.") Long id) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        return new ResponseEntity<>(priceListDtoMapper.toDto(priceListService.get(id, userDTO.getId())), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<PriceListDTO>> getAll() {
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        List<PriceListDTO> list = priceListService.getAll(userDTO.getId()).stream().map(priceListDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PriceListDTO> create(@Valid @RequestBody PriceListDTO priceListDTO) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        PriceList priceList = priceListService.create(priceListDtoMapper.toEntity(priceListDTO), userDTO.getId());
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CPL", String.format("User %s successfully created price list %s", userDTO.getId(), priceList.getId())));
        return new ResponseEntity<>(priceListDtoMapper.toDto(priceList), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PriceListDTO> edit(@PathVariable @Positive(message = "Id must be positive.") Long id, @Valid @RequestBody PriceListDTO priceListDTO) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        PriceList priceList = priceListService.edit(id, priceListDtoMapper.toEntity(priceListDTO), userDTO.getId());
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "EPL", String.format("User %s successfully edited price list %s", userDTO.getId(), priceList.getId())));
        return new ResponseEntity<>(priceListDtoMapper.toDto(priceList), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive(message = "Id must be positive.") Long id) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        priceListService.delete(id, userDTO.getId());
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "DPL", String.format("User %s successfully deleted price list %s", userDTO.getId(), id)));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private UserDTO stringToObject(String user) {
        try {
            return objectMapper.readValue(user, UserDTO.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Autowired
    public PriceListController(PriceListService priceListService, PriceListDtoMapper priceListDtoMapper, HttpServletRequest request,
                               ObjectMapper objectMapper, LogProducer logProducer) {
        this.priceListService = priceListService;
        this.priceListDtoMapper = priceListDtoMapper;
        this.request = request;
        this.objectMapper = objectMapper;
        this.logProducer = logProducer;
    }


}
