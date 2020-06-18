package jvn.RentACar.controller;

import jvn.RentACar.dto.both.BodyStyleDTO;
import jvn.RentACar.dto.request.CreateBodyStyleDTO;
import jvn.RentACar.mapper.BodyStyleDtoMapper;
import jvn.RentACar.model.BodyStyle;
import jvn.RentACar.model.Log;
import jvn.RentACar.service.BodyStyleService;
import jvn.RentACar.service.LogService;
import jvn.RentACar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    private LogService logService;

    private UserService userService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BodyStyleDTO> create(@Valid @RequestBody CreateBodyStyleDTO bodyStyleDTO) {
        BodyStyle bodyStyle = bodyStyleService.create(bodyStyleDTO);
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CBS", String.format("User %s successfully created body style %s", userService.getLoginUser().getId(), bodyStyle.getId())));
        return new ResponseEntity<>(bodyStyleDtoMapper.toDto(bodyStyle), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BodyStyleDTO>> get() {
        List<BodyStyleDTO> list = bodyStyleService.get().stream().map(bodyStyleDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BodyStyleDTO> edit(@PathVariable @Positive(message = "Id must be positive.") Long id,
                                             @Valid @RequestBody BodyStyleDTO bodyStyleDTO) {
        BodyStyle bodyStyle = bodyStyleService.edit(id, bodyStyleDTO);
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "EBS", String.format("User %s successfully edited body style %s", userService.getLoginUser().getId(), bodyStyle.getId())));
        return new ResponseEntity<>(bodyStyleDtoMapper.toDto(bodyStyle), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") @Positive(message = "Id must be positive.") Long id) {
        bodyStyleService.delete(id);
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "DBS", String.format("User %s successfully deleted body style %s", userService.getLoginUser().getId(), id)));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Autowired
    public BodyStyleController(BodyStyleService bodyStyleService, BodyStyleDtoMapper bodyStyleDtoMapper, LogService logService,
                               UserService userService) {
        this.bodyStyleService = bodyStyleService;
        this.bodyStyleDtoMapper = bodyStyleDtoMapper;
        this.logService = logService;
        this.userService = userService;
    }
}
