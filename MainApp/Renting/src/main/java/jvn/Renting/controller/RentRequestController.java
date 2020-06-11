package jvn.Renting.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Renting.dto.both.*;
import jvn.Renting.exceptionHandler.InvalidRentRequestDataException;
import jvn.Renting.mapper.CommentDtoMapper;
import jvn.Renting.mapper.MessageDtoMapper;
import jvn.Renting.mapper.RentRequestDtoMapper;
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

    private RentRequestService rentRequestService;

    private RentRequestDtoMapper rentRequestDtoMapper;

    private CommentDtoMapper commentDtoMapper;

    private MessageDtoMapper messageDtoMapper;

    private ObjectMapper objectMapper;

    private HttpServletRequest request;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RentRequestDTO> create(@Valid @RequestBody RentRequestDTO rentRequestDTO) {
        try {
            UserDTO userDTO = stringToObject(request.getHeader("user"));
            return new ResponseEntity<>(rentRequestDtoMapper.toDto(rentRequestService.create(rentRequestDtoMapper.toEntity(rentRequestDTO), userDTO)),
                    HttpStatus.CREATED);
        } catch (DateTimeParseException | ParseException e) {
            throw new InvalidRentRequestDataException("Please choose valid date and time.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{status}/advertisement/{advertisementId}")
    public ResponseEntity<List<RentRequestDTO>> getRentRequests(@PathVariable(value = "advertisementId", required = false) @Positive(message = "Id must be positive.") Long advertisementId,
                                                                @PathVariable(value = "status", required = false) @Pattern(regexp = "(?i)(all|pending|reserved|paid|canceled)$", message = "Status is not valid.")
                                                                        String status) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        return new ResponseEntity<>(rentRequestService.get(advertisementId, status, userDTO.getId()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentRequestDTO> get(@PathVariable @Positive(message = "Id must be positive.") Long id) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        return new ResponseEntity<>(rentRequestService.get(id, userDTO.getId()), HttpStatus.OK);
    }

    @GetMapping("/{status}/mine")
    public ResponseEntity<List<RentRequestDTO>> getMine(@PathVariable(value = "status") @Pattern(regexp = "(?i)(all|pending|reserved|paid|canceled)$", message = "Status is not valid.") String status) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        return new ResponseEntity<>(rentRequestService.getMine(status, userDTO.getId()), HttpStatus.OK);
    }


    @PostMapping(value="/{id}/rent-info/{rentInfoId}/comment")
    public ResponseEntity<CommentDTO> createComment(@PathVariable Long id, @PathVariable Long rentInfoId, @Valid @RequestBody CommentDTO commentDTO){
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        return new ResponseEntity<>(commentDtoMapper.toDto(rentRequestService.createComment(commentDtoMapper.toEntity(commentDTO),id, rentInfoId, userDTO.getId())),
                HttpStatus.CREATED);
    }

    @PostMapping(value="/{id}/rent-info/{rentInfoId}/feedback")
    public ResponseEntity<FeedbackDTO> leaveFeedback(@PathVariable Long id, @PathVariable Long rentInfoId, @Valid @RequestBody FeedbackDTO feedbackDTO){
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        return new ResponseEntity<>(rentRequestService.leaveFeedback(feedbackDTO, id, rentInfoId, new Long(1)),
                HttpStatus.CREATED);
    }

    @GetMapping(value="/{id}/rent-info/{rentInfoId}/feedback")
    public ResponseEntity<FeedbackDTO> getFeedback(@PathVariable Long id, @PathVariable Long rentInfoId){
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        return new ResponseEntity<>(rentRequestService.getFeedback(id, rentInfoId, new Long(1)), HttpStatus.OK);
    }

    @MessageMapping("/message")
    public ResponseEntity<MessageDTO> createMessage(String message){
        System.out.println("SLANJE PORUKE");
//        System.out.println(messageDTO.getText());
//        System.out.println(id);
        MessageDTO messageDTO = parseMessage(message);
        System.out.println(messageDTO.getText());
        System.out.println(messageDTO.getDateAndTime());
        System.out.println(messageDTO.getRentRequest().getId());
        System.out.println(messageDTO.getSender().getEmail());
//        UserDTO userDTO = stringToObject(request.getHeader("user"));
        return new ResponseEntity<>(messageDtoMapper.toDto(rentRequestService.createMessage(messageDtoMapper.toEntity(messageDTO), messageDTO.getRentRequest().getId(), new Long(1))),
                HttpStatus.CREATED);
    }

    @GetMapping(value="/{id}/message")
    public ResponseEntity<List<MessageDTO>> getMessages(@PathVariable Long id){
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        List<MessageDTO> list = rentRequestService.getMessages(id, new Long(1)).stream().map(messageDtoMapper::toDto).
                collect(Collectors.toList());
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

    @SuppressWarnings("unchecked")
    private MessageDTO parseMessage(String message) {
        System.out.println("PARSIRANJE STRINGA");
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
                                 ObjectMapper objectMapper, HttpServletRequest request) {
        this.rentRequestService = rentRequestService;
        this.rentRequestDtoMapper = rentRequestDtoMapper;
        this.commentDtoMapper = commentDtoMapper;
        this.messageDtoMapper = messageDtoMapper;
        this.objectMapper = objectMapper;
        this.request = request;
    }

}
