package jvn.Cars.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Cars.dto.both.MakeDTO;
import jvn.Cars.dto.both.ModelDTO;
import jvn.Cars.dto.message.Log;
import jvn.Cars.dto.request.UserDTO;
import jvn.Cars.mapper.MakeDtoMapper;
import jvn.Cars.mapper.ModelDtoMapper;
import jvn.Cars.model.Make;
import jvn.Cars.model.Model;
import jvn.Cars.producer.LogProducer;
import jvn.Cars.service.MakeService;
import jvn.Cars.service.ModelService;
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
@RequestMapping(value = "/api/make", produces = MediaType.APPLICATION_JSON_VALUE)
public class MakeController {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private MakeService makeService;

    private MakeDtoMapper makeDtoMapper;

    private ModelDtoMapper modelDtoMapper;

    private ModelService modelService;

    private LogProducer logProducer;

    private HttpServletRequest request;

    private ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MakeDTO> create(@Valid @RequestBody MakeDTO makeDTO) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));

        Make make = makeService.create(makeDtoMapper.toEntity(makeDTO));
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CMK", String.format("User %s successfully created make %s", userDTO.getId(), make.getId())));
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
        UserDTO userDTO = stringToObject(request.getHeader("user"));

        Make make = makeService.edit(id, makeDTO);
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "EMK", String.format("User %s successfully edited make %s", userDTO.getId(), make.getId())));
        return new ResponseEntity<>(makeDtoMapper.toDto(make), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") @Positive(message = "Id must be positive.") Long id) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));

        makeService.delete(id);
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "DMK", String.format("User %s successfully deleted make %s", userDTO.getId(), id)));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/{makeId}/model", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ModelDTO> createModel(@Valid @RequestBody ModelDTO modelDTO, @PathVariable @Positive(message = "Id must be positive.") Long makeId) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));

        Model model = modelService.create(modelDtoMapper.toEntity(modelDTO), makeService.get(makeId));
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CML", String.format("User %s successfully created model %s of make %s", userDTO.getId(), model.getId(), makeId)));
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
        UserDTO userDTO = stringToObject(request.getHeader("user"));

        Model model = modelService.edit(modelId, modelDTO, makeId);
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "EML", String.format("User %s successfully edited model %s of make %s", userDTO.getId(), model.getId(), makeId)));
        return new ResponseEntity<>(modelDtoMapper.toDto(model), HttpStatus.OK);
    }

    @DeleteMapping("/{makeId}/model/{modelId}")
    public ResponseEntity<Void> delete(@PathVariable @Positive(message = "Id must be positive.") Long makeId,
                                       @PathVariable @Positive(message = "Id must be positive.") Long modelId) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));

        modelService.delete(modelId, makeId);
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "DML", String.format("User %s successfully deleted model %s of make %s", userDTO.getId(), modelId, makeId)));
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
    public MakeController(MakeService makeService, MakeDtoMapper makeDtoMapper, ModelDtoMapper modelDtoMapper, ModelService modelService,
                          LogProducer logProducer, HttpServletRequest request, ObjectMapper objectMapper) {
        this.makeService = makeService;
        this.makeDtoMapper = makeDtoMapper;
        this.modelDtoMapper = modelDtoMapper;
        this.modelService = modelService;
        this.logProducer = logProducer;
        this.request = request;
        this.objectMapper = objectMapper;
    }
}
