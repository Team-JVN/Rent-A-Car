package jvn.RentACar.serviceImpl;

import jvn.RentACar.enumeration.RentRequestStatus;
import jvn.RentACar.exceptionHandler.InvalidCarDataException;
import jvn.RentACar.model.RentInfo;
import jvn.RentACar.repository.RentInfoRepository;
import jvn.RentACar.service.RentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RentInfoServiceImpl implements RentInfoService {

    private RentInfoRepository rentInfoRepository;

    @Override
    public List<RentInfo> getPaidRentInfos(Long carId) {
        return rentInfoRepository.findByAdvertisementCarIdAndRentRequestRentRequestStatusOrderByDateTimeToDesc(carId, RentRequestStatus.PAID);
    }

    @Override
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
