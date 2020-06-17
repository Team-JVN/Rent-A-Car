package jvn.Users.mapper;

import jvn.Users.dto.soap.agent.AgentDetails;
import jvn.Users.model.Agent;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AgentDetailsMapper implements MapperInterface<Agent, AgentDetails> {

    public ModelMapper modelMapper;

    @Override
    public Agent toEntity(AgentDetails dto) {
        Agent admin = modelMapper.map(dto, Agent.class);
        return admin;
    }

    @Override
    public AgentDetails toDto(Agent entity) {
        AgentDetails adminDTO = modelMapper.map(entity, AgentDetails.class);
        return adminDTO;
    }

    @Autowired
    public AgentDetailsMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}

