package jvn.RentACar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.RentACar.dto.both.CarDTO;
import jvn.RentACar.dto.both.CarWithPicturesDTO;
import jvn.RentACar.dto.request.CreateCarDTO;
import jvn.RentACar.exceptionHandler.InvalidCarDataException;
import jvn.RentACar.mapper.CarMapperImpl;
import jvn.RentACar.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
@RequestMapping(value = "/api/car")
public class CarController {

    private CarService carService;

    private CarMapperImpl carMapper;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CarDTO> create(@RequestParam("carData") String jsonString, @RequestParam("files") List<MultipartFile> multipartFiles) throws ParseException {

        ObjectMapper mapper = new ObjectMapper();
        CreateCarDTO createCarDTO = null;
        try {
            createCarDTO = mapper.readValue(jsonString, CreateCarDTO.class);
            validateCreateCarDTO(createCarDTO);
        } catch (IOException e) {
            throw new InvalidCarDataException("Please enter valid data.", HttpStatus.BAD_REQUEST);
        }
        CarDTO carDTO = carMapper.convertToCarDto(carService.create(carMapper.convertToEntity(createCarDTO), multipartFiles));
        return new ResponseEntity<>(carDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CarWithPicturesDTO>> get() {
        return new ResponseEntity<>(carService.get(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/picture", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<Resource> get(@PathVariable Long id, @RequestParam(value = "fileName", required = true) String fileName) {
        return new ResponseEntity<>(carService.get(fileName), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        carService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Autowired
    public CarController(CarService carService, CarMapperImpl carMapper) {
        this.carService = carService;
        this.carMapper = carMapper;
    }

    private void validateCreateCarDTO(CreateCarDTO createCarDTO) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<CreateCarDTO>> violations = validator.validate(createCarDTO);
        if (!violations.isEmpty()) {
            throw new InvalidCarDataException("Please enter valid data.", HttpStatus.BAD_REQUEST);
        }
    }
}
