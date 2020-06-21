package jvn.RentACar.service;

import jvn.RentACar.dto.both.FeedbackDTO;
import jvn.RentACar.dto.request.RentRequestStatusDTO;
import jvn.RentACar.model.Comment;
import jvn.RentACar.model.Message;
import jvn.RentACar.model.RentRequest;

import java.util.List;

public interface RentRequestService {
    RentRequest create(RentRequest rentRequestDTO);

    List<RentRequest> getMine(String status);

    List<RentRequest> get(Long advertisementId, String status);

    RentRequest get(Long id);

    RentRequest changeRentRequestStatus(Long id, RentRequestStatusDTO status);

    void rejectAllRequests(Long advId);


}
