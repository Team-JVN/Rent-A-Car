package jvn.SearchService.serviceImpl;

import jvn.SearchService.dto.SearchParamsDTO;
import jvn.SearchService.enumeration.LogicalStatus;
import jvn.SearchService.exceptionHandler.InvalidSearchDataException;
import jvn.SearchService.model.Advertisement;
import jvn.SearchService.repository.AdvertisementRepository;
import jvn.SearchService.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class AdvertisementServiceImpl implements AdvertisementService {

    private AdvertisementRepository advertisementRepository;

    @Override
    public List<Advertisement> getAll() {
        return advertisementRepository.findByLogicalStatus(LogicalStatus.EXISTING);
    }

    @Override
    public List<Advertisement> getAllMy(String status, Long id) {
        List<Advertisement> ads;
        switch (status) {
            case "all":
                ads = advertisementRepository.findAllByLogicalStatusNotAndOwner(LogicalStatus.DELETED, id);
                break;
            case "active":
                ads = advertisementRepository.findAllByLogicalStatusNotAndOwnerAndDateToEqualsOrLogicalStatusNotAndOwnerAndDateToGreaterThan(LogicalStatus.DELETED, id, null,
                        LogicalStatus.DELETED, id, LocalDate.now());
                break;
            case "operation_pending":
                ads = advertisementRepository.findAllByLogicalStatusAndOwner(LogicalStatus.OPERATION_PENDING, id);
                break;
            default:
                ads = advertisementRepository.findAllByLogicalStatusNotAndOwnerAndDateToLessThanEqual(LogicalStatus.DELETED, id, LocalDate.now());
                break;
        }
        return ads;
    }

    public List<Advertisement> searchAdvertisements(SearchParamsDTO searchParamsDTO) {
        LocalDateTime dateTimeFrom = getDateConverted(searchParamsDTO.getDateTimeFrom());
        LocalDateTime dateTimeTo = getDateConverted(searchParamsDTO.getDateTimeTo());
        LocalDateTime todayPlus48h = (LocalDateTime.now().minusMinutes(3)).plusDays(2);
        if (dateTimeFrom.isBefore(todayPlus48h)) {
            throw new InvalidSearchDataException("Date and time from must be at least 48h from now.", HttpStatus.BAD_REQUEST);
        }
        if (dateTimeTo.isBefore(todayPlus48h)) {
            throw new InvalidSearchDataException("Date and time to must be at least 48h from now.", HttpStatus.BAD_REQUEST);
        }

        List<Advertisement> searchedAdsList;
        if (searchParamsDTO.getCdw() != null && searchParamsDTO.getCdw()) {
            searchedAdsList = advertisementRepository.findByCDWAndLogicalStatusAndDateFromLessThanEqualAndDateToGreaterThanEqualAndKilometresLimitGreaterThanEqualAndPickUpPointContainsIgnoringCaseAndCarMakeAndCarModelAndCarFuelTypeAndCarGearBoxTypeAndCarBodyStyleAndCarMileageInKmLessThanEqualAndCarKidsSeatsGreaterThanEqualAndCarAvgRatingGreaterThanEqualAndPriceListPricePerDayBetween(
                    searchParamsDTO.getCdw(), LogicalStatus.EXISTING, dateTimeFrom.toLocalDate(), dateTimeTo.toLocalDate(), searchParamsDTO.getKilometresLimit(),
                    searchParamsDTO.getPickUpPoint(), searchParamsDTO.getMake(), searchParamsDTO.getModel(), searchParamsDTO.getFuelType(),
                    searchParamsDTO.getGearBoxType(), searchParamsDTO.getBodyStyle(), searchParamsDTO.getMileageInKm(), searchParamsDTO.getKidsSeats(),
                    searchParamsDTO.getMinRating().doubleValue(), searchParamsDTO.getMinPricePerDay(), searchParamsDTO.getMaxPricePerDay()
            );
        } else {
            searchedAdsList = advertisementRepository.findByLogicalStatusAndDateFromLessThanEqualAndDateToGreaterThanEqualAndKilometresLimitGreaterThanEqualAndPickUpPointContainsIgnoringCaseAndCarMakeAndCarModelAndCarFuelTypeAndCarGearBoxTypeAndCarBodyStyleAndCarMileageInKmLessThanEqualAndCarKidsSeatsGreaterThanEqualAndCarAvgRatingGreaterThanEqualAndPriceListPricePerDayBetween(
                    LogicalStatus.EXISTING, dateTimeFrom.toLocalDate(), dateTimeTo.toLocalDate(), searchParamsDTO.getKilometresLimit(),
                    searchParamsDTO.getPickUpPoint(), searchParamsDTO.getMake(), searchParamsDTO.getModel(), searchParamsDTO.getFuelType(),
                    searchParamsDTO.getGearBoxType(), searchParamsDTO.getBodyStyle(), searchParamsDTO.getMileageInKm(), searchParamsDTO.getKidsSeats(),
                    searchParamsDTO.getMinRating().doubleValue(), searchParamsDTO.getMinPricePerDay(), searchParamsDTO.getMaxPricePerDay()
            );
        }

        return searchedAdsList;
    }

    private LocalDateTime getDateConverted(String dateTime) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(dateTime, formatter);
    }

    @Autowired
    public AdvertisementServiceImpl(AdvertisementRepository advertisementRepository) {
        this.advertisementRepository = advertisementRepository;
    }
}
