package jvn.RentACar.service;

import jvn.RentACar.model.Message;

import java.util.List;

public interface MessageService {

    Message createMessage(Message message, Long id);

    List<Message> getMessages(Long id);
}
