package jvn.RentACar.serviceImpl;

import jvn.RentACar.enumeration.RentRequestStatus;
import jvn.RentACar.model.RentInfo;
import jvn.RentACar.repository.RentInfoRepository;
import jvn.RentACar.service.RentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class RentInfoServiceImpl implements RentInfoService {

    private RentInfoRepository rentInfoRepository;

    @Override
    public List<RentInfo> getPaidRentInfos(Long carId) {
        return rentInfoRepository.findByAdvertisementCarIdAndRentRequestRentRequestStatusOrderByDateTimeToDesc(carId, RentRequestStatus.PAID);
    }

    @Override
    public void delete(Set<RentInfo> rentInfos) {
        for (RentInfo rentInfo : rentInfos) {
            delete(rentInfo);
        }
    }

    private void delete(RentInfo rentInfo) {
        rentInfo.setAdvertisement(null);
        rentInfo.setRentRequest(null);
        rentInfoRepository.deleteById(rentInfo.getId());
    }

    @Autowired
    public RentInfoServiceImpl(RentInfoRepository rentInfoRepository) {
        this.rentInfoRepository = rentInfoRepository;
    }
}
