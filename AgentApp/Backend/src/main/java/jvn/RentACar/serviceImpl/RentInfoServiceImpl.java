package jvn.RentACar.serviceImpl;

import jvn.RentACar.enumeration.RentRequestStatus;
import jvn.RentACar.model.RentInfo;
import jvn.RentACar.repository.RentInfoRepository;
import jvn.RentACar.service.RentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RentInfoServiceImpl implements RentInfoService {

    private RentInfoRepository rentInfoRepository;

    @Override
    public List<RentInfo> getPaidRentInfos(Long carId) {
        return rentInfoRepository.findByAdvertisementCarIdAndRentRequestRentRequestStatusOrderByDateTimeToDesc(carId, RentRequestStatus.PAID);
    }

    @Autowired
    public RentInfoServiceImpl(RentInfoRepository rentInfoRepository) {
        this.rentInfoRepository = rentInfoRepository;
    }
}
