package jvn.Renting.service;

import jvn.Renting.dto.both.RentRequestDTO;
import jvn.Renting.dto.request.RentRequestStatusDTO;
import jvn.Renting.enumeration.EditType;
import jvn.Renting.model.RentRequest;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

public interface RentRequestService {

    RentRequest create(RentRequest rentRequestDTO, Long loggedInUser, Boolean canCreateRent) throws ParseException;

    List<RentRequestDTO> get(Long advertisementId, String status, Long loggedInUserId, String jwt, String user);

    List<RentRequestDTO> getMine(String status, Long loggedInUserId, String jwt, String user);

    RentRequestDTO get(Long id, Long loggedInUserId, String jwt, String user);

    RentRequest changeRentRequestStatus(Long id, RentRequestStatusDTO status, Long loggedInUserId);

    EditType getAdvertisementEditType(Long advId);

    Boolean canDeleteAdvertisement(Long advId);

    Boolean hasRentInfos(List<Long> advIds);

    void checkDate(Long id, LocalDate advertisementDateFrom, LocalDate advertisementDateTo, LocalDate rentInfoDateFrom, LocalDate rentInfoDateTo);

    void checkIfCanAcceptRentRequest(RentRequest rentRequest);

    RentRequest getRentRequest(Long id);

    void hasDebt(Long loggedInUserId);

    List<RentRequest> getAll(Long loggedInUserId);
}
