package jvn.Renting.mapper;

import jvn.Renting.dto.both.MessageDTO;
import jvn.Renting.model.Message;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageDtoMapper implements MapperInterface<Message, MessageDTO>{

    private ModelMapper modelMapper;

    @Override
    public Message toEntity(MessageDTO dto) {
        Message entity = modelMapper.map(dto, Message.class);
        return entity;
    }

    @Override
    public MessageDTO toDto(Message entity) {
        MessageDTO dto = modelMapper.map(entity, MessageDTO.class);
        return dto;
    }

    @Autowired
    public MessageDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
