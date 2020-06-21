package jvn.RentACar.controller;

import jvn.RentACar.dto.both.RentReportDTO;
import jvn.RentACar.mapper.RentReportDtoMapper;
import jvn.RentACar.service.RentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping(value = "/api/rent-report", produces = MediaType.APPLICATION_JSON_VALUE)
public class RentReportController {

    private RentReportService rentReportService;

    private RentReportDtoMapper rentReportDtoMapper;

    @PostMapping(value = "/{rentInfoId}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RentReportDTO> create(@PathVariable Long rentInfoId, @Valid @RequestBody RentReportDTO rentReportDTO) throws ParseException {
        return new ResponseEntity<>(rentReportDtoMapper.toDto(rentReportService.create(rentReportDtoMapper.toEntity(rentReportDTO), rentInfoId)),
                HttpStatus.CREATED);

    }

    @GetMapping
    public ResponseEntity<List<RentReportDTO>> getAll() {
        List<RentReportDTO> list = rentReportService.getAll().stream().map(rentReportDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @Autowired
    public RentReportController(RentReportService rentReportService, RentReportDtoMapper rentReportDtoMapper) {
        this.rentReportService = rentReportService;
        this.rentReportDtoMapper = rentReportDtoMapper;
    }


}
