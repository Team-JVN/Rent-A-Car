package jvn.Renting.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Renting.dto.both.*;
import jvn.Renting.exceptionHandler.InvalidMessageDataException;
import jvn.Renting.exceptionHandler.InvalidRentRequestDataException;
import jvn.Renting.mapper.CommentDtoMapper;
import jvn.Renting.mapper.MessageDtoMapper;
import jvn.Renting.dto.both.RentInfoDTO;
import jvn.Renting.dto.both.RentRequestDTO;
import jvn.Renting.dto.both.UserDTO;
import jvn.Renting.dto.message.Log;
import jvn.Renting.dto.request.RentRequestStatusDTO;
import jvn.Renting.enumeration.EditType;
import jvn.Renting.exceptionHandler.InvalidRentRequestDataException;
import jvn.Renting.mapper.RentInfoDtoMapper;
import jvn.Renting.mapper.RentRequestDtoMapper;
import jvn.Renting.model.RentInfo;
import jvn.Renting.model.RentRequest;
import jvn.Renting.producer.LogProducer;
import jvn.Renting.service.CommentService;
import jvn.Renting.service.MessageService;
import jvn.Renting.service.RentInfoService;
import jvn.Renting.service.RentRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.io.IOException;
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

    private CommentDtoMapper commentDtoMapper;

    private MessageDtoMapper messageDtoMapper;

    private ObjectMapper objectMapper;

    private HttpServletRequest request;

    private RentInfoService rentInfoService;

    private RentInfoDtoMapper rentInfoDtoMapper;

    private LogProducer logProducer;

    private MessageService messageService;

    private CommentService commentService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RentRequestDTO> create(@Valid @RequestBody RentRequestDTO rentRequestDTO) {
        try {
            UserDTO userDTO = stringToObject(request.getHeader("user"));
            RentRequest rentRequest = rentRequestService.create(rentRequestDtoMapper.toEntity(rentRequestDTO), userDTO.getId(), userDTO.getCanCreateRentRequests());
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CRQ", String.format("User %s successfully created rent request %s", userDTO.getId(), rentRequest.getId())));
            for (RentInfo rentInfo : rentRequest.getRentInfos()) {
                logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CRI", String.format("User %s successfully created rent info %s", userDTO.getId(), rentInfo.getId())));
            }
            return new ResponseEntity<>(rentRequestDtoMapper.toDto(rentRequest), HttpStatus.CREATED);
        } catch (DateTimeParseException | ParseException e) {
            throw new InvalidRentRequestDataException("Please choose valid date and time.", HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/{status}/advertisement/{advertisementId}")
    public ResponseEntity<List<RentRequestDTO>> getRentRequests(
            @PathVariable(value = "advertisementId", required = false) @Positive(message = "Id must be positive.") Long advertisementId,
            @PathVariable(value = "status", required = false) @Pattern(regexp = "(?i)(all|pending|reserved|paid|canceled)$", message = "Status is not valid.") String status) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        return new ResponseEntity<>(rentRequestService.get(advertisementId, status, userDTO.getId(), request.getHeader("Auth"), request.getHeader("user")), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentRequestDTO> get(@PathVariable @Positive(message = "Id must be positive.") Long id) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        return new ResponseEntity<>(rentRequestService.get(id, userDTO.getId(), request.getHeader("Auth"), request.getHeader("user")), HttpStatus.OK);
    }

    @GetMapping("/{status}/mine")
    public ResponseEntity<List<RentRequestDTO>> getMine(
            @PathVariable(value = "status") @Pattern(regexp = "(?i)(all|pending|reserved|paid|canceled)$", message = "Status is not valid.") String status) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        return new ResponseEntity<>(rentRequestService.getMine(status, userDTO.getId(), request.getHeader("Auth"), request.getHeader("user")), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RentRequestDTO> changeRentRequestStatus(
            @PathVariable @Positive(message = "Id must be positive.") Long id,
            @Valid @RequestBody RentRequestStatusDTO status) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        RentRequest rentRequest = rentRequestService.changeRentRequestStatus(id, status, userDTO.getId());
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "SRQ", String.format("User %s successfully changed rent request %s status to %s", userDTO.getId(), rentRequest.getId(), rentRequest.getRentRequestStatus().toString())));
        return new ResponseEntity<>(rentRequestDtoMapper.toDto(rentRequest), HttpStatus.OK);
    }

    @GetMapping(value = "/advertisement/{advId}/edit-type", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EditType> getAdvertisementEditType(
            @PathVariable("advId") @Positive(message = "Id must be positive.") Long id) {
        return new ResponseEntity<>(rentRequestService.getAdvertisementEditType(id), HttpStatus.OK);
    }

    @GetMapping(value = "/advertisement/{advId}/check-for-delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> canDeleteAdvertisement(
            @PathVariable("advId") @Positive(message = "Id must be positive.") Long advId) {
        return new ResponseEntity<>(rentRequestService.canDeleteAdvertisement(advId), HttpStatus.OK);
    }

    @GetMapping(value = "/advertisement/{advIds}/check-rent-infos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> hasRentInfos(@PathVariable("advIds") List<Long> advIds) {
        return new ResponseEntity<>(rentRequestService.hasRentInfos(advIds), HttpStatus.OK);
    }

    @PutMapping(value = "/{rentRequestId}/rent-info/{rentInfoId}/pay")
    public ResponseEntity<RentInfoDTO> pay(
            @PathVariable("rentRequestId") @Positive(message = "Id must be positive.") Long rentRequestId,
            @PathVariable("rentInfoId") @Positive(message = "Id must be positive.") Long rentInfoId) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        rentInfoService.pay(rentRequestId, rentInfoId, userDTO.getId());
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "PAY", String.format("User %s successfully paid for rent info %s from rent request %s", userDTO.getId(), rentInfoId, rentRequestId)));
        return new ResponseEntity<>(rentInfoDtoMapper.toDto(rentInfoService.pay(rentRequestId, rentInfoId, userDTO.getId())), HttpStatus.OK);
    }

    @PostMapping(value="/{id}/rent-info/{rentInfoId}/comment")
    public ResponseEntity<CommentDTO> createComment(@PathVariable Long id, @PathVariable Long rentInfoId, @Valid @RequestBody CommentDTO commentDTO){
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        commentDTO.setSender(userDTO);
        return new ResponseEntity<>(commentDtoMapper.toDto(commentService.createComment(commentDtoMapper.toEntity(commentDTO),id, rentInfoId, userDTO.getId())),
                HttpStatus.CREATED);
    }

    @PostMapping(value="/{id}/rent-info/{rentInfoId}/feedback")
    public ResponseEntity<FeedbackDTO> leaveFeedback(@PathVariable Long id, @PathVariable Long rentInfoId, @Valid @RequestBody FeedbackDTO feedbackDTO){
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        return new ResponseEntity<>(commentService.leaveFeedback(feedbackDTO, id, rentInfoId, userDTO.getId(), userDTO.getName()),
                HttpStatus.CREATED);
    }

    @GetMapping(value="/{id}/rent-info/{rentInfoId}/feedback")
    public ResponseEntity<FeedbackDTO> getFeedback(@PathVariable Long id, @PathVariable Long rentInfoId){

        UserDTO userDTO = stringToObject(request.getHeader("user"));

        return new ResponseEntity<>(commentService.getFeedback(id, rentInfoId, userDTO.getId()), HttpStatus.OK);
    }

    @PostMapping("/{id}/message")
    public ResponseEntity<MessageDTO> createMessage(@PathVariable Long id, @Valid @RequestBody MessageDTO messageDTO){
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        messageDTO.setSender(userDTO);
        return new ResponseEntity<>(messageDtoMapper.toDto(messageService.createMessage(messageDtoMapper.toEntity(messageDTO), id, userDTO.getId(), userDTO.getEmail())),
                HttpStatus.CREATED);
    }

    @GetMapping(value="/{id}/message")
    public ResponseEntity<List<MessageDTO>> getMessages(@PathVariable Long id){
        //TODO: need to handle exception when there is no messages
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        List<MessageDTO> list;
        list= messageService.getMessages(id, userDTO.getId()).stream().map(messageDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    private UserDTO stringToObject(String user) {
        try {
            return objectMapper.readValue(user, UserDTO.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private MessageDTO parseMessage(String message) {
        ObjectMapper mapper = new ObjectMapper();
        MessageDTO retVal;

        try {
            retVal = mapper.readValue(message, MessageDTO.class); // parsiranje JSON stringa
        } catch (IOException e) {
            retVal = null;
        }

        return retVal;
    }

    @Autowired
    public RentRequestController(RentRequestService rentRequestService, RentRequestDtoMapper rentRequestDtoMapper,
                                 CommentDtoMapper commentDtoMapper, MessageDtoMapper messageDtoMapper,
                                 ObjectMapper objectMapper, HttpServletRequest request, RentInfoService rentInfoService,
                                 RentInfoDtoMapper rentInfoDtoMapper, LogProducer logProducer, MessageService messageService,
                                 CommentService commentService) {
        this.rentRequestService = rentRequestService;
        this.rentRequestDtoMapper = rentRequestDtoMapper;
        this.commentDtoMapper = commentDtoMapper;
        this.messageDtoMapper = messageDtoMapper;
        this.objectMapper = objectMapper;
        this.request = request;
        this.rentInfoService = rentInfoService;
        this.rentInfoDtoMapper = rentInfoDtoMapper;
        this.logProducer = logProducer;
        this.messageService = messageService;
        this.commentService = commentService;
    }

}
