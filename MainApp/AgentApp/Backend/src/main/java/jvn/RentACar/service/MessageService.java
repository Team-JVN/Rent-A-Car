package jvn.RentACar.service;

import jvn.RentACar.model.Message;

import java.util.List;

public interface MessageService {

    Message createMessage(Message message, Long id, Long userId, String userEmail);

    List<Message> getMessages(Long id, Long userId);
}
