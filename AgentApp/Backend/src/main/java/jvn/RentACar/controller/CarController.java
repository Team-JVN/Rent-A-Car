package jvn.RentACar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.RentACar.dto.both.CarDTO;
import jvn.RentACar.dto.request.CreateCarDTO;
import jvn.RentACar.exceptionHandler.InvalidCarDataException;
import jvn.RentACar.model.Car;
import jvn.RentACar.service.CarService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/car", produces = MediaType.APPLICATION_JSON_VALUE)
public class CarController {

    private CarService carService;

    private ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<CarDTO> create(@RequestParam("carData") String jsonString, @RequestParam("files") List<MultipartFile> multipartFiles) throws ParseException {

        ObjectMapper mapper = new ObjectMapper();
        CreateCarDTO createCarDTO = null;
        try {
            createCarDTO = mapper.readValue(jsonString, CreateCarDTO.class);
            validateCreateCarDTO(createCarDTO);
        } catch (IOException e) {
            throw new InvalidCarDataException("Please enter valid data.");
        }
        CarDTO carDTO = convertToCarDto(carService.create(convertToEntity(createCarDTO), multipartFiles));
        return new ResponseEntity<>(carDTO, HttpStatus.CREATED);
    }

    @Autowired
    public CarController(CarService carService, ModelMapper modelMapper) {
        this.carService = carService;
        this.modelMapper = modelMapper;
    }

    private CreateCarDTO convertToDto(Car car) {
        CreateCarDTO createCarDTO = modelMapper.map(car, CreateCarDTO.class);
        return createCarDTO;
    }

    private CarDTO convertToCarDto(Car car) {
        CarDTO carDTO = modelMapper.map(car, CarDTO.class);
        return carDTO;
    }

    private Car convertToEntity(CreateCarDTO createCarDTO) throws ParseException {
        Car car = modelMapper.map(createCarDTO, Car.class);
        return car;
    }

    private void validateCreateCarDTO(CreateCarDTO createCarDTO) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<CreateCarDTO>> violations = validator.validate(createCarDTO);
        if (!violations.isEmpty()) {
            throw new InvalidCarDataException("Please enter valid data.");
        }
    }
}
