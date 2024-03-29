package jvn.Renting.mapper;

import jvn.Renting.dto.both.MessageDTO;
import jvn.Renting.model.Message;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class MessageDtoMapper implements MapperInterface<Message, MessageDTO>{

    private ModelMapper modelMapper;

    private RentRequestDtoMapper rentRequestDtoMapper;

    @Override
    public Message toEntity(MessageDTO dto) {

        Message entity = new Message();
        entity.setId(dto.getId());
        entity.setText(dto.getText());
        entity.setSenderId(dto.getSender().getId());
        entity.setSenderEmail(dto.getSender().getEmail());
        entity.setDateAndTime(getLocalDateTime(dto.getDateAndTime()));
        return entity;
    }

    @Override
    public MessageDTO toDto(Message entity) {
        MessageDTO dto = modelMapper.map(entity, MessageDTO.class);
        return dto;
    }
    private LocalDateTime getLocalDateTime(String date) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date.substring(0, 10), formatter);
        LocalTime localTime = LocalTime.parse(date.substring(11), DateTimeFormatter.ofPattern("HH:mm"));
        return LocalDateTime.of(localDate, localTime);
    }

    @Autowired
    public MessageDtoMapper(ModelMapper modelMapper, RentRequestDtoMapper rentRequestDtoMapper) {
        this.modelMapper = modelMapper;
        this.rentRequestDtoMapper = rentRequestDtoMapper;
    }
}
