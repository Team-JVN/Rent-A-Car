package jvn.RentACar.serviceImpl;

import jvn.RentACar.client.AgentClient;
import jvn.RentACar.enumeration.AgentStatus;
import jvn.RentACar.exceptionHandler.InvalidAgentDataException;
import jvn.RentACar.model.Agent;
import jvn.RentACar.model.User;
import jvn.RentACar.repository.AgentRepository;
import jvn.RentACar.service.AgentService;
import jvn.Users.dto.soap.agent.AgentDetails;
import jvn.Users.dto.soap.agent.GetProfileAgentDetailsRequest;
import jvn.Users.dto.soap.agent.GetProfileAgentDetailsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AgentServiceImpl implements AgentService {

    private AgentRepository agentRepository;

    private AgentClient agentClient;

    @Override
    public Agent edit(Long id, Agent agent) {
        Agent dbAgent = get(id);
        dbAgent.setName(agent.getName());
        dbAgent.setAddress(agent.getAddress());
        dbAgent.setPhoneNumber(agent.getPhoneNumber());
        dbAgent.setTaxIdNumber(agent.getTaxIdNumber());
        dbAgent = agentRepository.save(dbAgent);
        agentClient.edit(dbAgent);
        return dbAgent;
    }

    @Override
    public Agent get(Long id) {
        synchronize();
        Agent agent = agentRepository.findOneByIdAndStatusNot(id, AgentStatus.INACTIVE);
        if (agent == null) {
            throw new InvalidAgentDataException("Requested agent does not exist.", HttpStatus.NOT_FOUND);
        }
        return agent;
    }

    private void synchronize(){
        GetProfileAgentDetailsResponse response = agentClient.getProfile();
        AgentDetails agentDetails = response.getAgentDetails();
        if(agentDetails != null){
            Agent agent =agentRepository.findByEmail(agentDetails.getEmail());
            if(agent != null){
                agent.setName(agentDetails.getName());
                agent.setAddress(agentDetails.getAddress());
                agent.setPhoneNumber(agentDetails.getPhoneNumber());
                agent.setTaxIdNumber(agentDetails.getTaxIdNumber());
            }
        }

    }
    @Autowired
    public AgentServiceImpl(AgentRepository agentRepository,AgentClient agentClient) {
        this.agentRepository = agentRepository;
        this.agentClient =agentClient;
    }
}
