package jvn.RentACar.client;

import jvn.RentACar.dto.soap.agent.GetAgentDetailsRequest;
import jvn.RentACar.dto.soap.agent.GetAgentDetailsResponse;
import jvn.RentACar.dto.soap.agent.*;
import jvn.RentACar.mapper.AgentDetailsMapper;
import jvn.RentACar.model.Agent;
import jvn.RentACar.model.User;
import jvn.RentACar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

public class AgentClient extends WebServiceGatewaySupport {

    @Autowired
    private UserService userService;

    @Autowired
    private AgentDetailsMapper agentDetailsMapper;

    public GetAgentDetailsResponse edit(Agent agent) {

        GetAgentDetailsRequest request = new GetAgentDetailsRequest();
        request.setAgentDetails(agentDetailsMapper.toDto(agent));
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());
        GetAgentDetailsResponse response = (GetAgentDetailsResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

    public GetProfileAgentDetailsResponse getProfile() {
        GetProfileAgentDetailsRequest request = new GetProfileAgentDetailsRequest();
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());
        GetProfileAgentDetailsResponse response = (GetProfileAgentDetailsResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }
}
