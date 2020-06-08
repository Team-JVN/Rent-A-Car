package jvn.RentACar.serviceImpl;

import jvn.RentACar.enumeration.RentRequestStatus;
import jvn.RentACar.exceptionHandler.InvalidCarDataException;
import jvn.RentACar.exceptionHandler.InvalidRentRequestDataException;
import jvn.RentACar.model.RentInfo;
import jvn.RentACar.repository.RentInfoRepository;
import jvn.RentACar.service.RentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Override
    public RentInfo pay(Long rentRequestId, Long rentInfoId, Long loggedInUserId) {
        RentInfo rentInfo = rentInfoRepository.findByIdAndRentRequestIdAndRentRequestRentRequestStatusAndRentReportPaidAndRentRequestClientId(
                rentInfoId, rentRequestId, RentRequestStatus.PAID, false, loggedInUserId);
        if (rentInfo == null) {
            throw new InvalidRentRequestDataException("This rent info is already paid.", HttpStatus.BAD_REQUEST);
        }
        rentInfo.getRentReport().setPaid(true);
        return rentInfoRepository.save(rentInfo);
    }

    private void delete(RentInfo rentInfo) {
        rentInfo.setAdvertisement(null);
        rentInfo.setRentRequest(null);
        rentInfoRepository.deleteById(rentInfo.getId());
    }

    public RentInfo get(Long id) {
        RentInfo rentInfo = rentInfoRepository.findOneById(id);
        if (rentInfo == null) {
            throw new InvalidCarDataException("This RentInfo doesn't exist.", HttpStatus.NOT_FOUND);
        }
        return rentInfo;
    }

    @Autowired
    public RentInfoServiceImpl(RentInfoRepository rentInfoRepository) {
        this.rentInfoRepository = rentInfoRepository;
    }
}
