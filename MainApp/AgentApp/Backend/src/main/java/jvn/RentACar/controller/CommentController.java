package jvn.RentACar.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.RentACar.dto.both.CommentDTO;
import jvn.RentACar.dto.both.FeedbackDTO;
import jvn.RentACar.dto.both.UserDTO;
import jvn.RentACar.mapper.CommentDtoMapper;
import jvn.RentACar.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping(value = "/api/comment", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommentController {

    private CommentDtoMapper commentDtoMapper;

    private ObjectMapper objectMapper;

    private HttpServletRequest request;

    private CommentService commentService;

    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> get(@PathVariable Long id){
        return new ResponseEntity<>(commentDtoMapper.toDto(commentService.get(id)), HttpStatus.OK);
    }

    @GetMapping("/{status}/status")
    public ResponseEntity<List<CommentDTO>> getAll(@PathVariable(value = "status") @Pattern(regexp = "(?i)(all|awaiting|approved)$", message = "Status is not valid.") String status){
        System.out.println("getAll comments controller");

        List<CommentDTO> list;
        list = commentService.getAll(status).stream().map(commentDtoMapper::toDto).
                collect(Collectors.toList());

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PutMapping(value="/{id}/approve")
    public ResponseEntity<CommentDTO> approve(@PathVariable Long id, @Valid @RequestBody CommentDTO commentDTO){

        return new ResponseEntity<>(commentDtoMapper.toDto(commentService.approve(id)), HttpStatus.OK);
    }

    @PutMapping(value="/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable Long id, @Valid @RequestBody CommentDTO commentDTO){
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        commentService.reject(id, userDTO.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
    public CommentController(CommentDtoMapper commentDtoMapper, ObjectMapper objectMapper, HttpServletRequest request, CommentService commentService) {
        this.commentDtoMapper = commentDtoMapper;
        this.objectMapper = objectMapper;
        this.request = request;
        this.commentService = commentService;
    }
}
