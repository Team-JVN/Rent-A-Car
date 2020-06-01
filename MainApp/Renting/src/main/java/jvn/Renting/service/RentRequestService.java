package jvn.Renting.service;

import jvn.Renting.dto.both.RentRequestDTO;
import jvn.Renting.dto.both.UserDTO;
import jvn.Renting.model.RentRequest;

import java.text.ParseException;
import java.util.List;

public interface RentRequestService {

    RentRequest create(RentRequest rentRequestDTO, UserDTO loggedInUser) throws ParseException;

    List<RentRequestDTO> get(Long advertisementId, String status, Long loggedInUserId);

    List<RentRequestDTO> getMine(String status, Long loggedInUserId);

    RentRequestDTO get(Long id, Long loggedInUserId);
}
