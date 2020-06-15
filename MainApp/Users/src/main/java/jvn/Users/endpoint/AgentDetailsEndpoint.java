package jvn.Users.endpoint;

import jvn.Users.dto.response.UserInfoDTO;
import jvn.Users.dto.soap.agent.GetAgentDetailsRequest;
import jvn.Users.dto.soap.agent.GetAgentDetailsResponse;
import jvn.Users.mapper.AgentDetailsMapper;
import jvn.Users.model.Agent;
import jvn.Users.service.AgentService;
import jvn.Users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class AgentDetailsEndpoint {

    private static final String NAMESPACE_URI = "http://www.soap.dto/agent";

    private AgentService agentService;

    private UserService userService;

    private AgentDetailsMapper agentDetailsMapper;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAgentDetailsRequest")
    @ResponsePayload
    public GetAgentDetailsResponse createOrEdit(@RequestPayload GetAgentDetailsRequest request) {
        UserInfoDTO user = userService.getByEmail(request.getEmail());
        if (user == null) {
            return null;
        }
        Agent agent = agentService.edit(user.getId(), agentDetailsMapper.toEntity(request.getAgentDetails()));
        if (agent == null) {
            return null;
        }
        GetAgentDetailsResponse response = new GetAgentDetailsResponse();
        response.setAgentDetails(agentDetailsMapper.toDto(agent));
        return response;
    }

    @Autowired
    public AgentDetailsEndpoint(AgentService agentService, UserService userService, AgentDetailsMapper agentDetailsMapper) {
        this.agentService = agentService;
        this.userService = userService;
        this.agentDetailsMapper = agentDetailsMapper;
    }
}
