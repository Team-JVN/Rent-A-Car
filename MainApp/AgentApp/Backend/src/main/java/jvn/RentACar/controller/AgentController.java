package jvn.RentACar.controller;

import jvn.RentACar.dto.both.AgentDTO;
import jvn.RentACar.exceptionHandler.InvalidAgentDataException;
import jvn.RentACar.mapper.AgentDtoMapper;
import jvn.RentACar.model.Agent;
import jvn.RentACar.model.User;
import jvn.RentACar.service.AgentService;
import jvn.RentACar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping(value = "/api/agent", produces = MediaType.APPLICATION_JSON_VALUE)
public class AgentController {

    private AgentService agentService;

    private AgentDtoMapper agentDtoMapper;

    private UserService userService;


    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AgentDTO> edit(@Valid @RequestBody AgentDTO agentDTO) {
        User user = userService.getLoginUser();
        if (user instanceof Agent) {
            return new ResponseEntity<>(agentDtoMapper.toDto(agentService.edit(userService.getLoginUser().getId(), agentDtoMapper.toEntity(agentDTO))), HttpStatus.OK);
        }
        throw new InvalidAgentDataException("As a non-authorized user, you are not allowed to enter this page.", HttpStatus.FORBIDDEN);
    }

    @GetMapping(value = "/profile")
    public ResponseEntity<AgentDTO> getProfile() {
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
