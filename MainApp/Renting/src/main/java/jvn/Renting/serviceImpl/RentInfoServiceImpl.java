package jvn.Renting.serviceImpl;

import jvn.Renting.enumeration.RentRequestStatus;
import jvn.Renting.exceptionHandler.InvalidRentRequestDataException;
import jvn.Renting.model.RentInfo;
import jvn.Renting.repository.RentInfoRepository;
import jvn.Renting.service.RentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class RentInfoServiceImpl implements RentInfoService {

    private RentInfoRepository rentInfoRepository;

    @Override
    public RentInfo pay(Long rentRequestId, Long rentInfoId, Long loggedInUserId) {
        RentInfo rentInfo = rentInfoRepository.findByIdAndRentRequestIdAndRentRequestRentRequestStatusAndRentRequestClient(
                rentInfoId, rentRequestId, RentRequestStatus.PAID, loggedInUserId);
        if (rentInfo == null) {
            throw new InvalidRentRequestDataException("This rent info is already paid.", HttpStatus.BAD_REQUEST);
        }
        rentInfo.getRentReport().setPaid(true);
        return rentInfoRepository.save(rentInfo);
    }


    @Override
    public RentInfo pay(Long rentRequestId, Long rentInfoId) {
        RentInfo rentInfo = rentInfoRepository.findByIdAndRentRequestIdAndRentRequestRentRequestStatus(
                rentInfoId, rentRequestId, RentRequestStatus.PAID);
        if (rentInfo != null) {
            rentInfo.getRentReport().setPaid(true);
            return rentInfoRepository.save(rentInfo);
        }
        return null;
    }

    @Autowired
    public RentInfoServiceImpl(RentInfoRepository rentInfoRepository) {
        this.rentInfoRepository = rentInfoRepository;
    }
}
