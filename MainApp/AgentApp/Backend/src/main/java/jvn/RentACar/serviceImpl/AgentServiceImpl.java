package jvn.RentACar.serviceImpl;

import jvn.RentACar.enumeration.AgentStatus;
import jvn.RentACar.exceptionHandler.InvalidAgentDataException;
import jvn.RentACar.model.Agent;
import jvn.RentACar.repository.AgentRepository;
import jvn.RentACar.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AgentServiceImpl implements AgentService {

    private AgentRepository agentRepository;

    @Override
    public Agent edit(Long id, Agent agent) {
        Agent dbAgent = get(id);
        dbAgent.setName(agent.getName());
        dbAgent.setAddress(agent.getAddress());
        dbAgent.setPhoneNumber(agent.getPhoneNumber());
        dbAgent.setTaxIdNumber(agent.getTaxIdNumber());
        dbAgent = agentRepository.save(dbAgent);
        return dbAgent;
    }

    private Agent get(Long id) {
        Agent agent = agentRepository.findOneByIdAndStatusNot(id, AgentStatus.INACTIVE);
        if (agent == null) {
            throw new InvalidAgentDataException("Requested agent does not exist.", HttpStatus.NOT_FOUND);
        }
        return agent;
    }
    @Autowired
    public AgentServiceImpl(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }
}
