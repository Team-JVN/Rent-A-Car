package jvn.RentACar.service;

import jvn.RentACar.model.RentInfo;

import java.util.List;

public interface RentInfoService {
    List<RentInfo> getPaidRentInfos(Long carId);

    RentInfo get(Long id);
}
