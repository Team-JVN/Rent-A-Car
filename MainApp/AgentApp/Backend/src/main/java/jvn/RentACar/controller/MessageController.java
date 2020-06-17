package jvn.RentACar.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.RentACar.dto.both.MessageDTO;
import jvn.RentACar.dto.both.UserDTO;
import jvn.RentACar.mapper.MessageDtoMapper;
import jvn.RentACar.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping(value = "/api/message", produces = MediaType.APPLICATION_JSON_VALUE)
public class MessageController {

    private ObjectMapper objectMapper;

    private HttpServletRequest request;

    private MessageService messageService;

    private MessageDtoMapper messageDtoMapper;

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
            //TODO: Add to log and delete return null;
            return null;
        }
    }

    @Autowired
    public MessageController(ObjectMapper objectMapper, HttpServletRequest request, MessageService messageService, MessageDtoMapper messageDtoMapper) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.messageService = messageService;
        this.messageDtoMapper = messageDtoMapper;
    }
}
