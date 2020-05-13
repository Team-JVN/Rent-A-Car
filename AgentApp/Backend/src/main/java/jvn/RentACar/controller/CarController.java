package jvn.RentACar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.RentACar.dto.both.CarDTO;
import jvn.RentACar.dto.request.CarEditDTO;
import jvn.RentACar.dto.request.CreateCarDTO;
import jvn.RentACar.dto.response.CarWithPicturesDTO;
import jvn.RentACar.enumeration.EditType;
import jvn.RentACar.exceptionHandler.InvalidCarDataException;
import jvn.RentACar.mapper.CarDtoMapper;
import jvn.RentACar.mapper.CarWithPicturesDtoMapper;
import jvn.RentACar.mapper.CreateCarDtoMapper;
import jvn.RentACar.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Positive;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Validated
@RestController
@CrossOrigin
@RequestMapping(value = "/api/car")
public class CarController {

    private CarService carService;

    private CarDtoMapper carMapper;

    private CreateCarDtoMapper createCarDtoMapper;

    private CarWithPicturesDtoMapper carWithPicturesDtoMapper;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<CarDTO> create(@RequestParam("carData") String jsonString, @RequestParam("files") List<MultipartFile> multipartFiles) {

        ObjectMapper mapper = new ObjectMapper();
        CreateCarDTO createCarDTO;
        try {
            createCarDTO = mapper.readValue(jsonString, CreateCarDTO.class);
            validateCreateCarDTO(createCarDTO);
        } catch (IOException e) {
            throw new InvalidCarDataException("Please enter valid data.", HttpStatus.BAD_REQUEST);
        }
        CarDTO carDTO = carMapper.toDto(carService.create(createCarDtoMapper.toEntity(createCarDTO), multipartFiles));
        return new ResponseEntity<>(carDTO, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<List<CarWithPicturesDTO>> get() {
        List<CarWithPicturesDTO> list = carService.get().stream().map(carWithPicturesDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/picture", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<Resource> get(@PathVariable @Positive(message = "Id must be positive.")  Long id,
                                        @RequestParam(value = "fileName") String fileName) {
        return new ResponseEntity<>(carService.get(fileName), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<Void> delete(@PathVariable("id") @Positive(message = "Id must be positive.")  Long id) {
        carService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/{id}/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<EditType> getEditType(@PathVariable @Positive(message = "Id must be positive.")  Long id) {
        return new ResponseEntity<>(carService.getEditType(id), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<CarDTO> editAll(@PathVariable @Positive(message = "Id must be positive.")  Long id, @RequestParam("carData") String jsonString,
                                          @RequestParam("files") List<MultipartFile> multipartFiles)  {

        ObjectMapper mapper = new ObjectMapper();
        CarDTO carDTO;
        try {
            carDTO = mapper.readValue(jsonString, CarDTO.class);
            validateCarDTO(carDTO);
        } catch (IOException e) {
            throw new InvalidCarDataException("Please enter valid data.", HttpStatus.BAD_REQUEST);
        }
        CarDTO newCarDTO = carMapper.toDto(carService.editAll(id, carDTO, multipartFiles));
        return new ResponseEntity<>(newCarDTO, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/partial", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<CarDTO> editPartial(@PathVariable @Positive(message = "Id must be positive.")  Long id,
                                              @RequestParam("carData") String jsonString, @RequestParam("files") List<MultipartFile> multipartFiles)  {
        ObjectMapper mapper = new ObjectMapper();
        CarEditDTO carEditDTO;
        try {
            carEditDTO = mapper.readValue(jsonString, CarEditDTO.class);
            validateCarEditDTO(carEditDTO);
        } catch (IOException e) {
            throw new InvalidCarDataException("Please enter valid data.", HttpStatus.BAD_REQUEST);
        }
        CarDTO newCarDTO = carMapper.toDto(carService.editPartial(id, carEditDTO, multipartFiles));
        return new ResponseEntity<>(newCarDTO, HttpStatus.OK);
    }


    private void validateCreateCarDTO(CreateCarDTO createCarDTO) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<CreateCarDTO>> violations = validator.validate(createCarDTO);
        if (!violations.isEmpty()) {
            throw new InvalidCarDataException("Please enter valid data.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateCarDTO(CarDTO carDTO) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<CarDTO>> violations = validator.validate(carDTO);
        if (!violations.isEmpty()) {
            throw new InvalidCarDataException("Please enter valid data.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateCarEditDTO(CarEditDTO carDTO) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<CarEditDTO>> violations = validator.validate(carDTO);
        if (!violations.isEmpty()) {
            throw new InvalidCarDataException("Please enter valid data.", HttpStatus.BAD_REQUEST);
        }
    }

    @Autowired
    public CarController(CarService carService, CarDtoMapper carMapper, CreateCarDtoMapper createCarDtoMapper,
                         CarWithPicturesDtoMapper carWithPicturesDtoMapper) {
        this.carService = carService;
        this.carMapper = carMapper;
        this.createCarDtoMapper = createCarDtoMapper;
        this.carWithPicturesDtoMapper = carWithPicturesDtoMapper;
    }
}
