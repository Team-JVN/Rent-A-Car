package jvn.RentACar.controller;

import jvn.RentACar.dto.both.AgentDTO;
import jvn.RentACar.exceptionHandler.InvalidAgentDataException;
import jvn.RentACar.mapper.AgentDtoMapper;
import jvn.RentACar.model.Agent;
import jvn.RentACar.model.Log;
import jvn.RentACar.model.User;
import jvn.RentACar.service.AgentService;
import jvn.RentACar.service.LogService;
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

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private AgentService agentService;

    private AgentDtoMapper agentDtoMapper;

    private UserService userService;

    private LogService logService;

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AgentDTO> edit(@Valid @RequestBody AgentDTO agentDTO) {
        User user = userService.getLoginUser();
        if (user instanceof Agent) {
            Agent agent = agentService.edit(userService.getLoginUser().getId(), agentDtoMapper.toEntity(agentDTO));
            logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "EAG", String.format("User %s successfully edited profile", userService.getLoginUser().getId())));
            return new ResponseEntity<>(agentDtoMapper.toDto(agent), HttpStatus.OK);
        }
        throw new InvalidAgentDataException("As a non-authorized user, you are not allowed to enter this page.", HttpStatus.FORBIDDEN);
    }

    @GetMapping(value = "/profile")
    public ResponseEntity<AgentDTO> getProfile() {
        User user = userService.getLoginUser();
        if (user instanceof Agent) {
            return new ResponseEntity<>(agentDtoMapper.toDto(agentService.get(user.getId())), HttpStatus.OK);
        }
        throw new InvalidAgentDataException("As a non-authorized user, you are not allowed to enter this page.", HttpStatus.FORBIDDEN);
    }

    @Autowired
    public AgentController(AgentService agentService, AgentDtoMapper agentDtoMapper,
                           UserService userService, LogService logService) {
        this.agentService = agentService;
        this.agentDtoMapper = agentDtoMapper;
        this.userService = userService;
        this.logService = logService;
    }
}
