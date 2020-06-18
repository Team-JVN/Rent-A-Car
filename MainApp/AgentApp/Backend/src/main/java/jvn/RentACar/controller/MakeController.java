package jvn.RentACar.controller;

import jvn.RentACar.dto.both.MakeDTO;
import jvn.RentACar.dto.both.ModelDTO;
import jvn.RentACar.mapper.MakeDtoMapper;
import jvn.RentACar.mapper.ModelDtoMapper;
import jvn.RentACar.model.Log;
import jvn.RentACar.model.Make;
import jvn.RentACar.model.Model;
import jvn.RentACar.service.LogService;
import jvn.RentACar.service.MakeService;
import jvn.RentACar.service.ModelService;
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
@RequestMapping(value = "/api/make", produces = MediaType.APPLICATION_JSON_VALUE)
public class MakeController {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private MakeService makeService;

    private MakeDtoMapper makeDtoMapper;

    private ModelDtoMapper modelDtoMapper;

    private ModelService modelService;

    private LogService logService;

    private UserService userService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MakeDTO> create(@Valid @RequestBody MakeDTO makeDTO) {
        Make make = makeService.create(makeDtoMapper.toEntity(makeDTO));
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CMK", String.format("User %s successfully created make %s", userService.getLoginUser().getId(), make.getId())));
        return new ResponseEntity<>(makeDtoMapper.toDto(make), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MakeDTO>> get() {
        List<MakeDTO> list = makeService.get().stream().map(makeDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MakeDTO> edit(@PathVariable @Positive(message = "Id must be positive.") Long id, @Valid @RequestBody MakeDTO makeDTO) {
        Make make = makeService.edit(id, makeDTO);
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "EMK", String.format("User %s successfully edited make %s", userService.getLoginUser().getId(), make.getId())));
        return new ResponseEntity<>(makeDtoMapper.toDto(make), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") @Positive(message = "Id must be positive.") Long id) {
        makeService.delete(id);
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "DMK", String.format("User %s successfully deleted make %s", userService.getLoginUser().getId(), id)));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/{makeId}/model", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ModelDTO> createModel(@Valid @RequestBody ModelDTO modelDTO, @PathVariable @Positive(message = "Id must be positive.") Long makeId) {
        Model model = modelService.create(modelDtoMapper.toEntity(modelDTO), makeService.get(makeId));
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CML", String.format("User %s successfully created model %s of make %s", userService.getLoginUser().getId(), model.getId(), makeId)));
        return new ResponseEntity<>(modelDtoMapper.toDto(model), HttpStatus.CREATED);
    }

    @GetMapping("/{makeId}/models")
    public ResponseEntity<List<ModelDTO>> getModels(@PathVariable @Positive(message = "Id must be positive.") Long makeId) {
        List<ModelDTO> list = modelService.getAll(makeId).stream().map(modelDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PutMapping(value = "/{makeId}/model/{modelId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ModelDTO> editModel(@PathVariable @Positive(message = "Id must be positive.") Long makeId,
                                              @PathVariable @Positive(message = "Id must be positive.") Long modelId,
                                              @Valid @RequestBody ModelDTO modelDTO) {
        Model model = modelService.edit(modelId, modelDTO, makeId);
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "EML", String.format("User %s successfully edited model %s of make %s", userService.getLoginUser().getId(), model.getId(), makeId)));
        return new ResponseEntity<>(modelDtoMapper.toDto(model), HttpStatus.OK);
    }

    @DeleteMapping("/{makeId}/model/{modelId}")
    public ResponseEntity<Void> delete(@PathVariable @Positive(message = "Id must be positive.") Long makeId,
                                       @PathVariable @Positive(message = "Id must be positive.") Long modelId) {
        modelService.delete(modelId, makeId);
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "DML", String.format("User %s successfully deleted model %s of make %s", userService.getLoginUser().getId(), modelId, makeId)));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Autowired
    public MakeController(MakeService makeService, MakeDtoMapper makeDtoMapper, ModelDtoMapper modelDtoMapper, ModelService modelService,
                          LogService logService, UserService userService) {
        this.makeService = makeService;
        this.makeDtoMapper = makeDtoMapper;
        this.modelDtoMapper = modelDtoMapper;
        this.modelService = modelService;
        this.logService = logService;
        this.userService = userService;
    }
}
