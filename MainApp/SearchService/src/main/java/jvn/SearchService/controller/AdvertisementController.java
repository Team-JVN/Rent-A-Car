package jvn.SearchService.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.SearchService.dto.SearchParamsDTO;
import jvn.SearchService.dto.UserDTO;
import jvn.SearchService.model.Advertisement;
import jvn.SearchService.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.util.List;

@Validated
@RestController
@RequestMapping(value = "/api/advertisement", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdvertisementController {

    private AdvertisementService advertisementService;

    private ObjectMapper objectMapper;

    private HttpServletRequest request;

    @GetMapping("/{id}")
    public ResponseEntity<Advertisement> get(@PathVariable @Positive(message = "Id must be positive.") Long id) {
        return new ResponseEntity<>(advertisementService.get(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Advertisement>> getAll() {
        return new ResponseEntity<>(advertisementService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/all/{status}")
    public ResponseEntity<List<Advertisement>> getAllMine(
            @PathVariable(value = "status", required = false) @Pattern(regexp = "(?i)(all|active|inactive|operation_pending)$", message = "Status is not valid.") String status) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        return new ResponseEntity<>(advertisementService.getAllMy(status, userDTO.getId()), HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<List<Advertisement>> searchAdvertisements(@Valid @RequestBody SearchParamsDTO searchParamsDTO) {
        List<Advertisement> list = advertisementService.searchAdvertisements(searchParamsDTO);
        return new ResponseEntity<>(list, HttpStatus.OK);
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
    public AdvertisementController(AdvertisementService advertisementService, ObjectMapper objectMapper, HttpServletRequest request) {
        this.advertisementService = advertisementService;
        this.objectMapper = objectMapper;
        this.request = request;
    }
}
