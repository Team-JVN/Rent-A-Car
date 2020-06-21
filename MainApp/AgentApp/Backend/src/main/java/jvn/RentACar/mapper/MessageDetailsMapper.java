package jvn.RentACar.mapper;

import jvn.RentACar.dto.soap.message.MessageDetails;
import jvn.RentACar.exceptionHandler.InvalidMessageDataException;
import jvn.RentACar.exceptionHandler.InvalidRentRequestDataException;
import jvn.RentACar.model.Message;
import jvn.RentACar.service.UserService;
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

    private UserService userService;

    @Override
    public Message toEntity(MessageDetails dto) throws ParseException {
        Message message = new Message();
        message.setId(null);
        message.setMainAppId(dto.getId());
        message.setText(dto.getText());
        message.setDateAndTime(getLocalDateTime(dto.getDateAndTime()));
        message.setSender(userService.getByMainAppId(dto.getUserId()));
        return message;
    }

    @Override
    public MessageDetails toDto(Message entity) {
        MessageDetails details = new MessageDetails();
        details.setDateAndTime(getXMLGregorianCalendar(entity.getDateAndTime()));
        details.setText(entity.getText());
        details.setId(entity.getMainAppId());
        details.setUserId(entity.getSender().getMainAppId());
        details.setUserName(entity.getSender().getEmail());
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
    public MessageDetailsMapper(UserService userService) {
        this.userService = userService;
    }
}
