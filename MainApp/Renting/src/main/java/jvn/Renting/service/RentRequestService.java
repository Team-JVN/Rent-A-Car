package jvn.Renting.service;

import jvn.Renting.dto.both.FeedbackDTO;
import jvn.Renting.dto.both.RentRequestDTO;
import jvn.Renting.dto.both.UserDTO;
import jvn.Renting.model.Comment;
import jvn.Renting.model.Message;
import jvn.Renting.model.RentRequest;

import java.text.ParseException;
import java.util.BitSet;
import java.util.List;

public interface RentRequestService {

    RentRequest create(RentRequest rentRequestDTO, UserDTO loggedInUser) throws ParseException;

    List<RentRequestDTO> get(Long advertisementId, String status, Long loggedInUserId);

    List<RentRequestDTO> getMine(String status, Long loggedInUserId);

    RentRequestDTO get(Long id, Long loggedInUserId);

    Comment createComment(Comment comment, Long id, Long rentInfoId, Long userId);

    FeedbackDTO leaveFeedback(FeedbackDTO feedbackDTO, Long id, Long rentInfoId, Long userId);

    FeedbackDTO getFeedback(Long id, Long rentInfoId, Long userId);

    Message createMessage(Message message, Long id, Long userId);

    List<Message> getMessages(Long id, Long userId);
}
