package jvn.Renting.service;

import jvn.Renting.dto.both.UserDTO;
import jvn.Renting.model.RentRequest;

import java.text.ParseException;

public interface RentRequestService {

    RentRequest create(RentRequest rentRequestDTO, UserDTO loggedInUser) throws ParseException;
}
