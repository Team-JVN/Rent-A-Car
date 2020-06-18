package jvn.RentACar.controller;

import jvn.RentACar.dto.both.*;
import jvn.RentACar.dto.request.RentRequestStatusDTO;
import jvn.RentACar.exceptionHandler.InvalidAdvertisementDataException;
import jvn.RentACar.mapper.CommentDtoMapper;
import jvn.RentACar.mapper.MessageDtoMapper;
import jvn.RentACar.mapper.RentInfoDtoMapper;
import jvn.RentACar.mapper.RentRequestDtoMapper;
import jvn.RentACar.service.CommentService;
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
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping(value = "/api/rent-request", produces = MediaType.APPLICATION_JSON_VALUE)
public class RentRequestController {

    private RentRequestService rentRequestService;

    private RentRequestDtoMapper rentRequestDtoMapper;

    private RentInfoService rentInfoService;

    private RentInfoDtoMapper rentInfoDtoMapper;

    private UserService userService;

    private MessageDtoMapper messageDtoMapper;

    private CommentDtoMapper commentDtoMapper;

    private CommentService commentService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RentRequestDTO> create(@Valid @RequestBody RentRequestDTO rentRequestDTO) {
        try {
            return new ResponseEntity<>(rentRequestDtoMapper.toDto(rentRequestService.create(rentRequestDtoMapper.toEntity(rentRequestDTO))),
                    HttpStatus.CREATED);
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
        return new ResponseEntity<>(rentRequestDtoMapper.toDto(rentRequestService.changeRentRequestStatus(id, status)), HttpStatus.OK);
    }

    @PutMapping(value = "/{rentRequestId}/rent-info/{rentInfoId}/pay")
    public ResponseEntity<RentInfoDTO> pay(@PathVariable("rentRequestId") @Positive(message = "Id must be positive.") Long rentRequestId,
                                           @PathVariable("rentInfoId") @Positive(message = "Id must be positive.") Long rentInfoId) {
        return new ResponseEntity<>(rentInfoDtoMapper.toDto(rentInfoService.pay(rentRequestId, rentInfoId,userService.getLoginUser().getId())), HttpStatus.OK);
    }
    @PostMapping("/{id}/message")
    public ResponseEntity<MessageDTO> createMessage(@PathVariable Long id, @Valid @RequestBody MessageDTO messageDTO){
        return new ResponseEntity<>(messageDtoMapper.toDto(rentRequestService.createMessage(messageDtoMapper.toEntity(messageDTO), id)),
                HttpStatus.CREATED);
    }

    @GetMapping(value="/{id}/message")
    public ResponseEntity<List<MessageDTO>> getMessages(@PathVariable Long id){
        List<MessageDTO> list;
        list= rentRequestService.getMessages(id).stream().map(messageDtoMapper::toDto).
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
                                 MessageDtoMapper messageDtoMapper, CommentDtoMapper commentDtoMapper, CommentService commentService) {
        this.rentRequestService = rentRequestService;
        this.rentRequestDtoMapper = rentRequestDtoMapper;
        this.rentInfoService = rentInfoService;
        this.rentInfoDtoMapper = rentInfoDtoMapper;
        this.userService = userService;
        this.messageDtoMapper = messageDtoMapper;
        this.commentDtoMapper = commentDtoMapper;
        this.commentService = commentService;
    }
}
