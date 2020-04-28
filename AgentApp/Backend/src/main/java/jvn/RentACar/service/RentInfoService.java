package jvn.RentACar.service;

import jvn.RentACar.model.RentInfo;

import java.util.List;
import java.util.Set;

public interface RentInfoService {
    List<RentInfo> getPaidRentInfos(Long carId);

    void delete(Set<RentInfo> rentInfos);

    RentInfo get(Long id);
}
