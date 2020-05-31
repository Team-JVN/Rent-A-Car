package jvn.Renting.service;

import jvn.Renting.model.RentRequest;

import java.text.ParseException;

public interface RentRequestService {

    RentRequest create(RentRequest rentRequestDTO, Long loggedInUserId) throws ParseException;
}
