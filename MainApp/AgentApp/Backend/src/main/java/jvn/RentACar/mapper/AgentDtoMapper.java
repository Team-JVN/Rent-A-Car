package jvn.RentACar.mapper;

import jvn.RentACar.dto.both.AgentDTO;
import jvn.RentACar.model.Agent;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AgentDtoMapper implements MapperInterface<Agent, AgentDTO>{

    public ModelMapper modelMapper;

    @Override
    public Agent toEntity(AgentDTO dto) {
        Agent agent = modelMapper.map(dto, Agent.class);
        return agent;
    }

    @Override
    public AgentDTO toDto(Agent entity) {
        AgentDTO agentDTO = modelMapper.map(entity, AgentDTO.class);
        return agentDTO;
    }

    @Autowired
    public AgentDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
