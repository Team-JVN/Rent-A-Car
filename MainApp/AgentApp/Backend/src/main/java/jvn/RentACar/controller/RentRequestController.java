package jvn.RentACar.controller;

import jvn.RentACar.dto.both.*;
import jvn.RentACar.dto.request.RentRequestStatusDTO;
import jvn.RentACar.exceptionHandler.InvalidAdvertisementDataException;
import jvn.RentACar.mapper.*;
import jvn.RentACar.model.User;
import jvn.RentACar.service.*;
import jvn.RentACar.model.Log;
import jvn.RentACar.model.RentInfo;
import jvn.RentACar.model.RentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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

    private MessageDtoMapper messageDtoMapper;

    private CommentDtoMapper commentDtoMapper;

    private CommentService commentService;

    private LogService logService;

    private UserDtoMapper userDtoMapper;

    private MessageService messageService;

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
    @PostMapping("/{id}/message")
    public ResponseEntity<MessageDTO> createMessage(@PathVariable Long id, @Valid @RequestBody MessageDTO messageDTO){
        User user = userService.getLoginUser();
        messageDTO.setSender(userDtoMapper.toDto(user));
        return new ResponseEntity<>(messageDtoMapper.toDto(messageService.createMessage(messageDtoMapper.toEntity(messageDTO), id)),
                HttpStatus.CREATED);
    }

    @GetMapping(value="/{id}/message")
    public ResponseEntity<List<MessageDTO>> getMessages(@PathVariable Long id){
        List<MessageDTO> list;
        list= messageService.getMessages(id).stream().map(messageDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping(value="/{id}/rent-info/{rentInfoId}/comment")
    public ResponseEntity<CommentDTO> createComment(@PathVariable Long id, @PathVariable Long rentInfoId, @Valid @RequestBody CommentDTO commentDTO){
        return new ResponseEntity<>(commentDtoMapper.toDto(commentService.createComment(commentDtoMapper.toEntity(commentDTO),id, rentInfoId)),
                HttpStatus.CREATED);
    }

    @PostMapping(value="/{id}/rent-info/{rentInfoId}/feedback")
    public ResponseEntity<FeedbackDTO> leaveFeedback(@PathVariable Long id, @PathVariable Long rentInfoId, @Valid @RequestBody FeedbackDTO feedbackDTO){
        if(feedbackDTO.getComments() != null){
            int i = 0;
            List<CommentDTO>commentDTOS = new ArrayList<>(feedbackDTO.getComments());
            for(CommentDTO commentDTO: commentDTOS){
                User user = userService.getLoginUser();
                commentDTO.setSender(userDtoMapper.toDto(user));
            }
        }
        return new ResponseEntity<>(commentService.leaveFeedback(feedbackDTO, id, rentInfoId),
                HttpStatus.CREATED);
    }

    @GetMapping(value="/{id}/rent-info/{rentInfoId}/feedback")
    public ResponseEntity<FeedbackDTO> getFeedback(@PathVariable Long id, @PathVariable Long rentInfoId){

        return new ResponseEntity<>(commentService.getFeedback(id, rentInfoId), HttpStatus.OK);
    }

    @Autowired
    public RentRequestController(RentRequestService rentRequestService, RentRequestDtoMapper rentRequestDtoMapper,
                                 RentInfoService rentInfoService, RentInfoDtoMapper rentInfoDtoMapper,UserService userService,
                                 MessageDtoMapper messageDtoMapper, CommentDtoMapper commentDtoMapper, CommentService commentService,
                                 LogService logService, UserDtoMapper userDtoMapper, MessageService messageService) {
        this.rentRequestService = rentRequestService;
        this.rentRequestDtoMapper = rentRequestDtoMapper;
        this.rentInfoService = rentInfoService;
        this.rentInfoDtoMapper = rentInfoDtoMapper;
        this.userService = userService;
        this.messageDtoMapper = messageDtoMapper;
        this.commentDtoMapper = commentDtoMapper;
        this.commentService = commentService;
        this.logService = logService;
        this.userDtoMapper = userDtoMapper;
        this.messageService = messageService;

    }
}
