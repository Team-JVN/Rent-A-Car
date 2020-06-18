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
        Agent entity = modelMapper.map(dto, Agent.class);
        return entity;
    }

    @Override
    public AgentDetails toDto(Agent entity) {
        AgentDetails dto = modelMapper.map(entity, AgentDetails.class);
        return dto;
    }

    @Autowired
    public AgentDetailsMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}

