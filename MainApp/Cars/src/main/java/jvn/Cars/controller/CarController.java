package jvn.Cars.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Cars.dto.both.CarDTO;
import jvn.Cars.dto.request.CarEditDTO;
import jvn.Cars.dto.request.CreateCarDTO;
import jvn.Cars.dto.request.UserDTO;
import jvn.Cars.dto.response.CarWithAllInformationDTO;
import jvn.Cars.dto.response.CarWithPicturesDTO;
import jvn.Cars.exceptionHandler.InvalidCarDataException;
import jvn.Cars.mapper.CarDtoMapper;
import jvn.Cars.mapper.CarWithAllInformationDtoMapper;
import jvn.Cars.mapper.CarWithPicturesDtoMapper;
import jvn.Cars.mapper.CreateCarDtoMapper;
import jvn.Cars.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Validated
@RestController
@CrossOrigin
@RequestMapping(value = "/api/car")
public class CarController {

    private CarService carService;

    private CarDtoMapper carDtoMapper;
    private CreateCarDtoMapper createCarDtoMapper;
    private CarWithPicturesDtoMapper carWithPicturesDtoMapper;
    private CarWithAllInformationDtoMapper carWithAllInformationDtoMapper;

    private HttpServletRequest request;

    private ObjectMapper objectMapper;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CarDTO> create(@RequestParam("carData") String jsonString, @RequestParam("files") List<MultipartFile> multipartFiles) {

        ObjectMapper mapper = new ObjectMapper();
        CreateCarDTO createCarDTO;
        try {
            createCarDTO = mapper.readValue(jsonString, CreateCarDTO.class);
            validateCreateCarDTO(createCarDTO);
        } catch (IOException e) {
            throw new InvalidCarDataException("Please enter valid data.", HttpStatus.BAD_REQUEST);
        }
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        CarDTO carDTO = carDtoMapper.toDto(carService.create(createCarDtoMapper.toEntity(createCarDTO), multipartFiles, userDTO.getId()));
        return new ResponseEntity<>(carDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CarWithPicturesDTO>> get() {
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        List<CarWithPicturesDTO> list = carService.get(userDTO).stream().map(carWithPicturesDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/verify/{userId}/{carId}")
    public ResponseEntity<CarWithAllInformationDTO> verify(@PathVariable("userId") @Positive(message = "Id must be positive.") Long userId,
                                                           @PathVariable("carId") @Positive(message = "Id must be positive.") Long carId) {
        return new ResponseEntity<>(carWithAllInformationDtoMapper.toDto(carService.get(carId, userId)), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/picture", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<Resource> get(@PathVariable @Positive(message = "Id must be positive.") Long id,
                                        @RequestParam(value = "fileName") String fileName) {
        return new ResponseEntity<>(carService.get(fileName), HttpStatus.OK);
    }

    @GetMapping(value = "/statistics/{filter}")
    public ResponseEntity<List<CarWithPicturesDTO>> getStatistics(
            @PathVariable(value = "filter") @Pattern(regexp = "(?i)(most-km-made|best-rated|most-commented)$", message = "Filter is not valid.") String filter) {

        return new ResponseEntity<>(carService.getStatistics(filter).stream().map(carWithPicturesDtoMapper::toDto).collect(Collectors.toList()), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") @Positive(message = "Id must be positive.") Long id) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        carService.delete(id, userDTO.getId(), request.getHeader("Auth"), request.getHeader("user"));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CarDTO> editAll(@PathVariable @Positive(message = "Id must be positive.") Long id, @RequestParam("carData") String jsonString,
                                          @RequestParam("files") List<MultipartFile> multipartFiles) {

        ObjectMapper mapper = new ObjectMapper();
        CarDTO carDTO;
        try {
            carDTO = mapper.readValue(jsonString, CarDTO.class);
            validateCarDTO(carDTO);
        } catch (IOException e) {
            throw new InvalidCarDataException("Please enter valid data.", HttpStatus.BAD_REQUEST);
        }

        UserDTO userDTO = stringToObject(request.getHeader("user"));
        return new ResponseEntity<>(carDtoMapper.toDto(carService.editAll(id, carDtoMapper.toEntity(carDTO), multipartFiles, userDTO.getId())), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/partial", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CarDTO> editPartial(@PathVariable @Positive(message = "Id must be positive.") Long id,
                                              @RequestParam("carData") String jsonString, @RequestParam("files") List<MultipartFile> multipartFiles) {
        ObjectMapper mapper = new ObjectMapper();
        CarEditDTO carEditDTO;
        try {
            carEditDTO = mapper.readValue(jsonString, CarEditDTO.class);
            validateCarEditDTO(carEditDTO);
        } catch (IOException e) {
            throw new InvalidCarDataException("Please enter valid data.", HttpStatus.BAD_REQUEST);
        }

        UserDTO userDTO = stringToObject(request.getHeader("user"));
        return new ResponseEntity<>(carDtoMapper.toDto(carService.editPartial(id, carEditDTO, multipartFiles, userDTO.getId(), request.getHeader("Auth"), request.getHeader("user"), userDTO)), HttpStatus.OK);
    }

    private void validateCreateCarDTO(CreateCarDTO createCarDTO) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<CreateCarDTO>> violations = validator.validate(createCarDTO);
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

    private void validateCarDTO(CarDTO carDTO) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<CarDTO>> violations = validator.validate(carDTO);
        if (!violations.isEmpty()) {
            throw new InvalidCarDataException("Please enter valid data.", HttpStatus.BAD_REQUEST);
        }
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
    public CarController(CarService carService, CarDtoMapper carDtoMapper, CreateCarDtoMapper createCarDtoMapper, HttpServletRequest request,
                         CarWithPicturesDtoMapper carWithPicturesDtoMapper, CarWithAllInformationDtoMapper carWithAllInformationDtoMapper,
                         ObjectMapper objectMapper) {
        this.carService = carService;
        this.carDtoMapper = carDtoMapper;
        this.createCarDtoMapper = createCarDtoMapper;
        this.carWithPicturesDtoMapper = carWithPicturesDtoMapper;
        this.carWithAllInformationDtoMapper = carWithAllInformationDtoMapper;
        this.request = request;
        this.objectMapper = objectMapper;
    }
}
