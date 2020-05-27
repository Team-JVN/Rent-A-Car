package jvn.Cars.controller;

import jvn.Cars.dto.both.MakeDTO;
import jvn.Cars.dto.both.ModelDTO;
import jvn.Cars.mapper.MakeDtoMapper;
import jvn.Cars.mapper.ModelDtoMapper;
import jvn.Cars.service.MakeService;
import jvn.Cars.service.ModelService;
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
    private MakeService makeService;

    private MakeDtoMapper makeDtoMapper;

    private ModelDtoMapper modelDtoMapper;

    private ModelService modelService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MakeDTO> create(@Valid @RequestBody MakeDTO makeDTO) {
        return new ResponseEntity<>(makeDtoMapper.toDto(makeService.create(makeDtoMapper.toEntity(makeDTO))), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MakeDTO>> get() {
        List<MakeDTO> list = makeService.get().stream().map(makeDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MakeDTO> edit(@PathVariable @Positive(message = "Id must be positive.") Long id, @Valid @RequestBody MakeDTO makeDTO) {
        return new ResponseEntity<>(makeDtoMapper.toDto(makeService.edit(id, makeDTO)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") @Positive(message = "Id must be positive.") Long id) {
        makeService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/{makeId}/model", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ModelDTO> createModel(@Valid @RequestBody ModelDTO modelDTO, @PathVariable @Positive(message = "Id must be positive.") Long makeId) {
        return new ResponseEntity<>(modelDtoMapper.toDto(modelService.create(modelDtoMapper.toEntity(modelDTO), makeService.get(makeId))), HttpStatus.CREATED);
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
        return new ResponseEntity<>(modelDtoMapper.toDto(modelService.edit(modelId, modelDTO, makeId)), HttpStatus.OK);
    }

    @DeleteMapping("/{makeId}/model/{modelId}")
    public ResponseEntity<Void> delete(@PathVariable @Positive(message = "Id must be positive.") Long makeId,
                                       @PathVariable @Positive(message = "Id must be positive.") Long modelId) {
        modelService.delete(modelId, makeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Autowired
    public MakeController(MakeService makeService, MakeDtoMapper makeDtoMapper, ModelDtoMapper modelDtoMapper, ModelService modelService) {
        this.makeService = makeService;
        this.makeDtoMapper = makeDtoMapper;
        this.modelDtoMapper = modelDtoMapper;
        this.modelService = modelService;
    }
}
