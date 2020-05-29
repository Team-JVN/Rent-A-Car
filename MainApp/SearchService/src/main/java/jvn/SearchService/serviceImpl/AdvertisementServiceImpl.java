package jvn.SearchService.serviceImpl;

import jvn.SearchService.dto.SearchParamsDTO;
import jvn.SearchService.model.Advertisement;
import jvn.SearchService.repository.AdvertisementRepository;
import jvn.SearchService.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdvertisementServiceImpl implements AdvertisementService {

    private AdvertisementRepository advertisementRepository;

    public List<Advertisement> searchAdvertisements(SearchParamsDTO searchParamsDTO) {
//        List<Advertisement> ads = null;
//        if (status.equals("all")) {
//            ads = advertisementRepository.findAllByLogicalStatusNot(LogicalStatus.DELETED);
//        } else if (status.equals("active")) {
//            ads = advertisementRepository.findAllByLogicalStatusNotAndDateToEqualsOrLogicalStatusNotAndDateToGreaterThan(LogicalStatus.DELETED, null, LogicalStatus.DELETED,
//                    LocalDate.now());
//        } else {
//            ads = advertisementRepository.findAllByLogicalStatusNotAndDateToLessThanEqual(LogicalStatus.DELETED, LocalDate.now());
//        }
//        return ads;
        return null;
    }

//    private void checkIfCarIsAvailable(Long carId, LocalDate advertisementDateFrom, LocalDate advertisementDateTo) {
//        if (!advertisementRepository.findByCarAndLogicalStatusNotAndDateToEquals(carId, LogicalStatus.DELETED, null).isEmpty()) {
//            throw new InvalidAdvertisementDataException("Active advertisement for this car already exist!", HttpStatus.BAD_REQUEST);
//        }
//
//        if (advertisementDateTo == null) {
//            if (!advertisementRepository.findByCarAndLogicalStatusNotAndDateToGreaterThanEqual(carId, LogicalStatus.DELETED, advertisementDateFrom).isEmpty()) {
//                throw new InvalidAdvertisementDataException("Active advertisement for this car already exist!", HttpStatus.BAD_REQUEST);
//            }
//        } else {
//            if (!advertisementRepository.findByCarAndLogicalStatusNotAndDateFromLessThanEqualAndDateToGreaterThanEqual(carId, LogicalStatus.DELETED, advertisementDateTo, advertisementDateFrom).isEmpty()) {
//                throw new InvalidAdvertisementDataException("Active advertisement for this car already exist!", HttpStatus.BAD_REQUEST);
//            }
//        }
//    }
//
//    private void checkDate(LocalDate dateFrom, LocalDate dateTo) {
//        if (dateFrom.isBefore(LocalDate.now()) || (dateTo != null && dateTo.isBefore(LocalDate.now()))) {
//            throw new InvalidAdvertisementDataException("Invalid date from/to.", HttpStatus.BAD_REQUEST);
//        }
//        if (dateTo != null && dateTo.isBefore(dateFrom)) {
//            throw new InvalidAdvertisementDataException("Date To cannot be before Date From.", HttpStatus.BAD_REQUEST);
//        }
//    }

    @Autowired
    public AdvertisementServiceImpl(AdvertisementRepository advertisementRepository) {
        this.advertisementRepository = advertisementRepository;
    }
}
