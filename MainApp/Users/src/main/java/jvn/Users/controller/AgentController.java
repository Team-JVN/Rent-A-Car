package jvn.Users.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Users.dto.both.AdminDTO;
import jvn.Users.dto.both.AgentDTO;
import jvn.Users.dto.response.UserDTO;
import jvn.Users.exceptionHandler.InvalidAgentDataException;
import jvn.Users.mapper.AgentDtoMapper;
import jvn.Users.model.Admin;
import jvn.Users.model.Agent;
import jvn.Users.model.User;
import jvn.Users.service.AgentService;
import jvn.Users.service.UserService;
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
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping(value = "/api/agent", produces = MediaType.APPLICATION_JSON_VALUE)
public class AgentController {

    private AgentService agentService;

    private AgentDtoMapper agentDtoMapper;

    private UserService userService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AgentDTO> create(@Valid @RequestBody AgentDTO agentDTO) {
        return new ResponseEntity<>(agentDtoMapper.toDto(agentService.create(agentDtoMapper.toEntity(agentDTO))), HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<AgentDTO> get(@PathVariable("id") @Positive(message = "Id must be positive.") Long id) {
        return new ResponseEntity<>(agentDtoMapper.toDto(agentService.get(id)), HttpStatus.OK);
    }

    @GetMapping(value = "/all/{status}")
    public ResponseEntity<List<AgentDTO>> getAll(@PathVariable(value = "status") @Pattern(regexp = "(?i)(all|active|inactive)$", message = "Status is not valid.") String status) {
        List<AgentDTO> list = agentService.getAll(status, userService.getLoginUser().getId()).stream().map(agentDtoMapper::toDto).
                collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") @Positive(message = "Id must be positive.") Long id) {
        agentService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AgentDTO> edit(@Valid @RequestBody AgentDTO agentDTO) {
        User user = userService.getLoginUser();
        if (user instanceof Agent) {
            return new ResponseEntity<>(agentDtoMapper.toDto(agentService.edit(userService.getLoginUser().getId(), agentDtoMapper.toEntity(agentDTO))), HttpStatus.OK);
        }
        throw new InvalidAgentDataException("As a non-authorized user, you are not allowed to enter this page.", HttpStatus.FORBIDDEN);
    }

    @GetMapping(value = "/logged-in-user")
    public ResponseEntity<AgentDTO> get() {
        User user = userService.getLoginUser();
        if (user instanceof Agent) {
            return new ResponseEntity<>(agentDtoMapper.toDto((Agent) userService.getLoginUser()), HttpStatus.OK);
        }
        throw new InvalidAgentDataException("As a non-authorized user, you are not allowed to enter this page.", HttpStatus.FORBIDDEN);
    }

    @Autowired
    public AgentController(AgentService agentService, AgentDtoMapper agentDtoMapper,
                           UserService userService) {
        this.agentService = agentService;
        this.agentDtoMapper = agentDtoMapper;
        this.userService = userService;
    }
}
