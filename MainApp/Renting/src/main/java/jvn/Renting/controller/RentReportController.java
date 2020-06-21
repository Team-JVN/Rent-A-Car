package jvn.Renting.controller;

import jvn.Renting.dto.both.RentReportDTO;
import jvn.Renting.mapper.RentReportDtoMapper;
import jvn.Renting.service.RentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Validated
@RestController
@RequestMapping(value = "/api/rent-report", produces = MediaType.APPLICATION_JSON_VALUE)
public class RentReportController {

    private RentReportService rentReportService;

    private RentReportDtoMapper rentReportDtoMapper;

    private HttpServletRequest request;

    @PostMapping(value = "/{rentInfoId}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RentReportDTO> create(@PathVariable Long rentInfoId, @Valid @RequestBody RentReportDTO rentReport) {

        return new ResponseEntity<>(rentReportDtoMapper.toDto(rentReportService.create(rentReportDtoMapper.toEntity(rentReport), rentInfoId)),
                HttpStatus.CREATED);
    }

    @Autowired
    public RentReportController(RentReportService rentReportService, RentReportDtoMapper rentReportDtoMapper,
                                HttpServletRequest request) {
        this.rentReportService = rentReportService;
        this.rentReportDtoMapper = rentReportDtoMapper;
        this.request = request;
    }
}
