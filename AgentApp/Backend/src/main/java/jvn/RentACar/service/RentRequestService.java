package jvn.RentACar.service;

import jvn.RentACar.model.RentRequest;

import java.util.List;

public interface RentRequestService {
    RentRequest create(RentRequest rentRequestDTO);

    List<RentRequest> getMine(String status);

    List<RentRequest> get(Long advertisementId, String status);

    RentRequest get(Long id);

    void delete(Long id);
}
