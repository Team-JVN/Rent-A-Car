package jvn.SearchService.serviceImpl;

import jvn.SearchService.dto.SearchParamsDTO;
import jvn.SearchService.enumeration.LogicalStatus;
import jvn.SearchService.exceptionHandler.InvalidAdvertisementDataException;
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
import java.util.stream.Collectors;

@Service
public class AdvertisementServiceImpl implements AdvertisementService {

    private AdvertisementRepository advertisementRepository;

    @Override
    public Advertisement get(Long id) {
        Advertisement advertisement = advertisementRepository.findByIdAndLogicalStatus(id, LogicalStatus.EXISTING);
        if (advertisement == null) {
            throw new InvalidAdvertisementDataException("Requested advertisement does not exist.", HttpStatus.NOT_FOUND);
        }
        return advertisement;
    }

    @Override
    public List<Advertisement> getAll() {
        return advertisementRepository.findByLogicalStatus(LogicalStatus.EXISTING);
    }

    @Override
    public List<Advertisement> getAllMy(String status, Long id) {
        List<Advertisement> ads;
        switch (status) {
            case "all":
                ads = advertisementRepository.findAllByLogicalStatusNotAndOwnerId(LogicalStatus.DELETED, id);
                break;
            case "active":
                ads = advertisementRepository.findAllByLogicalStatusNotAndOwnerIdAndDateToEqualsOrLogicalStatusNotAndOwnerIdAndDateToGreaterThan(LogicalStatus.DELETED, id, null,
                        LogicalStatus.DELETED, id, LocalDate.now());
                break;
            case "operation_pending":
                ads = advertisementRepository.findAllByLogicalStatusAndOwnerId(LogicalStatus.OPERATION_PENDING, id);
                break;
            default:
                ads = advertisementRepository.findAllByLogicalStatusNotAndOwnerIdAndDateToLessThanEqual(LogicalStatus.DELETED, id, LocalDate.now());
                break;
        }
        return ads;
    }

    @Override
    public List<Advertisement> get(List<Long> advertisements) {
        return advertisementRepository.findByIdInAndLogicalStatus(advertisements, LogicalStatus.EXISTING);
    }

    @Override
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

        SearchParamsDTO newSearchParamsDTO = replaceNullValues(searchParamsDTO);

        List<Advertisement> searchedAdsList;
        if (newSearchParamsDTO.getCDW() != null && newSearchParamsDTO.getCDW()) {
            searchedAdsList = advertisementRepository.findByCDWAndLogicalStatusAndDateFromLessThanEqualAndDateToIsNotNullAndDateToGreaterThanEqualAndPickUpPointContainsIgnoringCaseAndCarMakeContainsAndCarModelContainsAndCarFuelTypeContainsAndCarGearBoxTypeContainsAndCarBodyStyleContainsAndCarMileageInKmLessThanEqualAndCarKidsSeatsGreaterThanEqualAndCarAvgRatingGreaterThanEqualAndPriceListPricePerDayBetweenOrCDWAndLogicalStatusAndDateFromLessThanEqualAndDateToIsNullAndPickUpPointContainsIgnoringCaseAndCarMakeContainsAndCarModelContainsAndCarFuelTypeContainsAndCarGearBoxTypeContainsAndCarBodyStyleContainsAndCarMileageInKmLessThanEqualAndCarKidsSeatsGreaterThanEqualAndCarAvgRatingGreaterThanEqualAndPriceListPricePerDayBetween(
                    newSearchParamsDTO.getCDW(), LogicalStatus.EXISTING, dateTimeFrom.toLocalDate(), dateTimeTo.toLocalDate(), newSearchParamsDTO.getPickUpPoint(),
                    newSearchParamsDTO.getMake(), newSearchParamsDTO.getModel(), newSearchParamsDTO.getFuelType(), newSearchParamsDTO.getGearBoxType(),
                    newSearchParamsDTO.getBodyStyle(), newSearchParamsDTO.getMileageInKm(), newSearchParamsDTO.getKidsSeats(), newSearchParamsDTO.getMinRating().doubleValue(),
                    newSearchParamsDTO.getMinPricePerDay(), newSearchParamsDTO.getMaxPricePerDay(),
                    newSearchParamsDTO.getCDW(), LogicalStatus.EXISTING, dateTimeFrom.toLocalDate(), newSearchParamsDTO.getPickUpPoint(), newSearchParamsDTO.getMake(),
                    newSearchParamsDTO.getModel(), newSearchParamsDTO.getFuelType(), newSearchParamsDTO.getGearBoxType(), newSearchParamsDTO.getBodyStyle(),
                    newSearchParamsDTO.getMileageInKm(), newSearchParamsDTO.getKidsSeats(), newSearchParamsDTO.getMinRating().doubleValue(),
                    newSearchParamsDTO.getMinPricePerDay(), newSearchParamsDTO.getMaxPricePerDay()
            );
        } else {
            searchedAdsList = advertisementRepository.findByLogicalStatusAndDateFromLessThanEqualAndDateToIsNotNullAndDateToGreaterThanEqualAndPickUpPointContainsIgnoringCaseAndCarMakeContainsAndCarModelContainsAndCarFuelTypeContainsAndCarGearBoxTypeContainsAndCarBodyStyleContainsAndCarMileageInKmLessThanEqualAndCarKidsSeatsGreaterThanEqualAndCarAvgRatingGreaterThanEqualAndPriceListPricePerDayBetweenOrLogicalStatusAndDateFromLessThanEqualAndDateToIsNullAndPickUpPointContainsIgnoringCaseAndCarMakeContainsAndCarModelContainsAndCarFuelTypeContainsAndCarGearBoxTypeContainsAndCarBodyStyleContainsAndCarMileageInKmLessThanEqualAndCarKidsSeatsGreaterThanEqualAndCarAvgRatingGreaterThanEqualAndPriceListPricePerDayBetween(
                    LogicalStatus.EXISTING, dateTimeFrom.toLocalDate(), dateTimeTo.toLocalDate(), newSearchParamsDTO.getPickUpPoint(),
                    newSearchParamsDTO.getMake(), newSearchParamsDTO.getModel(), newSearchParamsDTO.getFuelType(), newSearchParamsDTO.getGearBoxType(),
                    newSearchParamsDTO.getBodyStyle(), newSearchParamsDTO.getMileageInKm(), newSearchParamsDTO.getKidsSeats(), newSearchParamsDTO.getMinRating().doubleValue(),
                    newSearchParamsDTO.getMinPricePerDay(), newSearchParamsDTO.getMaxPricePerDay(),
                    LogicalStatus.EXISTING, dateTimeFrom.toLocalDate(), newSearchParamsDTO.getPickUpPoint(), newSearchParamsDTO.getMake(),
                    newSearchParamsDTO.getModel(), newSearchParamsDTO.getFuelType(), newSearchParamsDTO.getGearBoxType(), newSearchParamsDTO.getBodyStyle(),
                    newSearchParamsDTO.getMileageInKm(), newSearchParamsDTO.getKidsSeats(), newSearchParamsDTO.getMinRating().doubleValue(),
                    newSearchParamsDTO.getMinPricePerDay(), newSearchParamsDTO.getMaxPricePerDay()
            );
        }

        searchedAdsList.stream().filter(ad -> ad.getKilometresLimit() == null || ad.getKilometresLimit() >= newSearchParamsDTO.getKilometresLimit()).collect(Collectors.toList());

        return searchedAdsList;
    }

    private LocalDateTime getDateConverted(String dateTime) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(dateTime, formatter);
    }

    private SearchParamsDTO replaceNullValues(SearchParamsDTO searchParamsDTO) {
        if (searchParamsDTO.getMake() == null) {
            searchParamsDTO.setMake("");
        }
        if (searchParamsDTO.getModel() == null) {
            searchParamsDTO.setModel("");
        }
        if (searchParamsDTO.getMinRating() == null) {
            searchParamsDTO.setMinRating(0);
        }
        if (searchParamsDTO.getFuelType() == null) {
            searchParamsDTO.setFuelType("");
        }
        if (searchParamsDTO.getGearBoxType() == null) {
            searchParamsDTO.setGearBoxType("");
        }
        if (searchParamsDTO.getBodyStyle() == null) {
            searchParamsDTO.setBodyStyle("");
        }
        if (searchParamsDTO.getMinPricePerDay() == null) {
            searchParamsDTO.setMinPricePerDay(0.0);
        }
        if (searchParamsDTO.getMaxPricePerDay() == null) {
            searchParamsDTO.setMaxPricePerDay(Double.MAX_VALUE);
        }
        if (searchParamsDTO.getKidsSeats() == null) {
            searchParamsDTO.setKidsSeats(0);
        }
        if (searchParamsDTO.getMileageInKm() == null) {
            searchParamsDTO.setMileageInKm(Integer.MAX_VALUE);
        }
        if (searchParamsDTO.getKilometresLimit() == null) {
            searchParamsDTO.setKilometresLimit(0);
        }

        return searchParamsDTO;
    }

    @Autowired
    public AdvertisementServiceImpl(AdvertisementRepository advertisementRepository) {
        this.advertisementRepository = advertisementRepository;
    }
}
