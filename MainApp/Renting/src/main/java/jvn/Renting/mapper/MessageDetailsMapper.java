package jvn.Renting.mapper;

import jvn.Renting.dto.soap.message.MessageDetails;
import jvn.Renting.exceptionHandler.InvalidMessageDataException;
import jvn.Renting.model.Message;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.time.LocalDateTime;

@Component
public class MessageDetailsMapper implements MapperInterface<Message, MessageDetails>{

    private ModelMapper modelMapper;

    @Override
    public Message toEntity(MessageDetails dto) {
        Message message = new Message();
        message.setId(dto.getId());
        message.setText(dto.getText());
        message.setDateAndTime(getLocalDateTime(dto.getDateAndTime()));
        message.setSenderEmail(dto.getUserName());
        message.setSenderId(dto.getUserId());
        return message;
    }

    @Override
    public MessageDetails toDto(Message entity) {
        MessageDetails details = new MessageDetails();
        details.setDateAndTime(getXMLGregorianCalendar(entity.getDateAndTime()));
        details.setText(entity.getText());
        details.setId(entity.getId());
        details.setUserId(entity.getSenderId());
        details.setUserName(entity.getSenderEmail());
        return details;
    }

    private LocalDateTime getLocalDateTime(XMLGregorianCalendar xmlGregorianCalendar) {
        return xmlGregorianCalendar.toGregorianCalendar().toZonedDateTime().toLocalDateTime();
    }

    private XMLGregorianCalendar getXMLGregorianCalendar(LocalDateTime localDateTime) {
        try {
            String dateTimeStr = localDateTime.toString();
            if (localDateTime.getSecond() == 0 && localDateTime.getNano() == 0) {
                dateTimeStr += ":00"; // necessary hack because the second part is not optional in XML
            }
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(dateTimeStr);
        } catch (DatatypeConfigurationException | IllegalArgumentException e) {
            throw new InvalidMessageDataException("Invalid date and time format.", HttpStatus.BAD_REQUEST);
        }
    }

    @Autowired
    public MessageDetailsMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
