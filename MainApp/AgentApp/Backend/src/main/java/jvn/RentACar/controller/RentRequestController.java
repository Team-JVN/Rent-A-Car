package jvn.RentACar.controller;

import jvn.RentACar.dto.both.RentInfoDTO;
import jvn.RentACar.dto.both.RentRequestDTO;
import jvn.RentACar.dto.request.RentRequestStatusDTO;
import jvn.RentACar.dto.response.UserDTO;
import jvn.RentACar.exceptionHandler.InvalidAdvertisementDataException;
import jvn.RentACar.mapper.RentInfoDtoMapper;
import jvn.RentACar.mapper.RentRequestDtoMapper;
import jvn.RentACar.model.Log;
import jvn.RentACar.model.RentInfo;
import jvn.RentACar.model.RentRequest;
import jvn.RentACar.service.LogService;
import jvn.RentACar.service.RentInfoService;
import jvn.RentACar.service.RentRequestService;
import jvn.RentACar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.text.ParseException;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping(value = "/api/rent-request", produces = MediaType.APPLICATION_JSON_VALUE)
public class RentRequestController {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private RentRequestService rentRequestService;

    private RentRequestDtoMapper rentRequestDtoMapper;

    private RentInfoService rentInfoService;

    private RentInfoDtoMapper rentInfoDtoMapper;

    private UserService userService;

    private LogService logService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RentRequestDTO> create(@Valid @RequestBody RentRequestDTO rentRequestDTO) {
        try {
            RentRequest rentRequest = rentRequestService.create(rentRequestDtoMapper.toEntity(rentRequestDTO));
            logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CRQ", String.format("User %s successfully created rent request %s", userService.getLoginUser().getId(), rentRequest.getId())));
            for (RentInfo rentInfo : rentRequest.getRentInfos()) {
                logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CRI", String.format("User %s successfully created rent info %s", userService.getLoginUser().getId(), rentInfo.getId())));
            }
            return new ResponseEntity<>(rentRequestDtoMapper.toDto(rentRequest), HttpStatus.CREATED);
        } catch (DateTimeParseException e) {
            throw new InvalidAdvertisementDataException("Please choose valid date and time.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{status}/mine")
    public ResponseEntity<List<RentRequestDTO>> getMine(@PathVariable(value = "status") @Pattern(regexp = "(?i)(all|pending|reserved|paid|canceled)$", message = "Status is not valid.") String status) {
        List<RentRequestDTO> list = rentRequestService.getMine(status).stream().map(rentRequestDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentRequestDTO> get(@PathVariable @Positive(message = "Id must be positive.") Long id) {
        return new ResponseEntity<>(rentRequestDtoMapper.toDto(rentRequestService.get(id)), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RentRequestDTO> changeRentRequestStatus(@PathVariable @Positive(message = "Id must be positive.") Long id,
                                                                  @Valid @RequestBody RentRequestStatusDTO status) {
        RentRequest rentRequest = rentRequestService.changeRentRequestStatus(id, status);
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "SRQ", String.format("User %s successfully changed rent request %s status to %s", userService.getLoginUser().getId(), rentRequest.getId(), rentRequest.getRentRequestStatus().toString())));
        return new ResponseEntity<>(rentRequestDtoMapper.toDto(rentRequest), HttpStatus.OK);
    }

    @PutMapping(value = "/{rentRequestId}/rent-info/{rentInfoId}/pay")
    public ResponseEntity<RentInfoDTO> pay(@PathVariable("rentRequestId") @Positive(message = "Id must be positive.") Long rentRequestId,
                                           @PathVariable("rentInfoId") @Positive(message = "Id must be positive.") Long rentInfoId) {
        RentInfo rentInfo = rentInfoService.pay(rentRequestId, rentInfoId, userService.getLoginUser().getId());
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "PAY", String.format("User %s successfully paid for rent info %s from rent request %s", userService.getLoginUser().getId(), rentInfoId, rentRequestId)));
        return new ResponseEntity<>(rentInfoDtoMapper.toDto(rentInfo), HttpStatus.OK);
    }

    @Autowired
    public RentRequestController(RentRequestService rentRequestService, RentRequestDtoMapper rentRequestDtoMapper, RentInfoService rentInfoService,
                                 RentInfoDtoMapper rentInfoDtoMapper, UserService userService, LogService logService) {
        this.rentRequestService = rentRequestService;
        this.rentRequestDtoMapper = rentRequestDtoMapper;
        this.rentInfoService = rentInfoService;
        this.rentInfoDtoMapper = rentInfoDtoMapper;
        this.userService = userService;
        this.logService = logService;
    }
}
