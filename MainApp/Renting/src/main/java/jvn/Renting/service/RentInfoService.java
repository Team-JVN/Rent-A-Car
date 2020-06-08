package jvn.Renting.service;

import jvn.Renting.model.RentInfo;

public interface RentInfoService {
    RentInfo pay(Long rentRequestId, Long rentInfoId, Long loggedInUserId);
}
