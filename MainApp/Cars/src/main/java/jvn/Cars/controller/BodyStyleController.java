package jvn.Cars.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Cars.dto.both.BodyStyleDTO;
import jvn.Cars.dto.message.Log;
import jvn.Cars.dto.request.CreateBodyStyleDTO;
import jvn.Cars.dto.request.UserDTO;
import jvn.Cars.mapper.BodyStyleDtoMapper;
import jvn.Cars.model.BodyStyle;
import jvn.Cars.producer.LogProducer;
import jvn.Cars.service.BodyStyleService;
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
@RequestMapping(value = "/api/body-style", produces = MediaType.APPLICATION_JSON_VALUE)
public class BodyStyleController {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private BodyStyleService bodyStyleService;

    private BodyStyleDtoMapper bodyStyleDtoMapper;

    private LogProducer logProducer;

    private HttpServletRequest request;

    private ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BodyStyleDTO> create(@Valid @RequestBody CreateBodyStyleDTO bodyStyleDTO) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));

        BodyStyle bodyStyle = bodyStyleService.create(bodyStyleDTO);
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CBS", String.format("User %s successfully created body style %s", userDTO.getId(), bodyStyle.getId())));
        return new ResponseEntity<>(bodyStyleDtoMapper.toDto(bodyStyle), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BodyStyleDTO>> get() {
        List<BodyStyleDTO> list = bodyStyleService.get().stream().map(bodyStyleDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BodyStyleDTO> edit(@PathVariable @Positive(message = "Id must be positive.") Long id, @Valid @RequestBody BodyStyleDTO bodyStyleDTO) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));

        BodyStyle bodyStyle = bodyStyleService.edit(id, bodyStyleDTO);
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "EBS", String.format("User %s successfully edited body style %s", userDTO.getId(), bodyStyle.getId())));
        return new ResponseEntity<>(bodyStyleDtoMapper.toDto(bodyStyle), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") @Positive(message = "Id must be positive.") Long id) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));

        bodyStyleService.delete(id);
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "DBS", String.format("User %s successfully deleted body style %s", userDTO.getId(), id)));
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
    public BodyStyleController(BodyStyleService bodyStyleService, BodyStyleDtoMapper bodyStyleDtoMapper,
                               LogProducer logProducer, HttpServletRequest request, ObjectMapper objectMapper) {
        this.bodyStyleService = bodyStyleService;
        this.bodyStyleDtoMapper = bodyStyleDtoMapper;
        this.logProducer = logProducer;
        this.request = request;
        this.objectMapper = objectMapper;
    }
}
