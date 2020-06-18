package jvn.RentACar.repository;

import jvn.RentACar.enumeration.LogicalStatus;
import jvn.RentACar.enumeration.RentRequestStatus;
import jvn.RentACar.model.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

    Advertisement findByIdAndLogicalStatus(Long id, LogicalStatus logicalStatus);

    Advertisement findByMainAppIdAndLogicalStatus(Long mainAppId, LogicalStatus logicalStatus);

    List<Advertisement> findAllByLogicalStatusNot(LogicalStatus logicalStatus);

    List<Advertisement> findAllByLogicalStatusNotAndDateToEqualsOrLogicalStatusNotAndDateToGreaterThan(LogicalStatus logicalStatus, LocalDate localDate, LogicalStatus logicalStatus1,
                                                                                                       LocalDate localDateToNow);

    List<Advertisement> findAllByLogicalStatusNotAndDateToLessThanEqual(LogicalStatus logicalStatus1, LocalDate localDateToNow);

    Advertisement findByIdAndRentInfosRentRequestRentRequestStatusNotAndLogicalStatus(Long id, RentRequestStatus status, LogicalStatus logicalStatus);

    List<Advertisement> findByCarIdAndLogicalStatusNotAndDateToEquals(Long id, LogicalStatus status, LocalDate localDate);

    List<Advertisement> findByCarIdAndLogicalStatusNotAndDateToGreaterThanEqual(Long id, LogicalStatus status, LocalDate localDateFrom);

    List<Advertisement> findByCarIdAndLogicalStatusNotAndDateFromLessThanEqualAndDateToGreaterThanEqual(Long id, LogicalStatus status, LocalDate localDateTo, LocalDate localDateFrom);

    Advertisement findByIdAndLogicalStatusAndRentInfosRentRequestRentRequestStatusAndRentInfosDateTimeToGreaterThanEqual(Long id, LogicalStatus logicalStatus, RentRequestStatus status, LocalDateTime localDateTime);

    // With CDW available
    List<Advertisement> findByCDWAndLogicalStatusAndDateFromLessThanEqualAndDateToIsNotNullAndDateToGreaterThanEqualAndPickUpPointContainsIgnoringCaseAndCarMakeNameContainsAndCarModelNameContainsAndCarFuelTypeNameContainsAndCarGearBoxTypeNameContainsAndCarBodyStyleNameContainsAndCarMileageInKmLessThanEqualAndCarKidsSeatsGreaterThanEqualAndCarAvgRatingGreaterThanEqualAndPriceListPricePerDayBetweenOrCDWAndLogicalStatusAndDateFromLessThanEqualAndDateToIsNullAndPickUpPointContainsIgnoringCaseAndCarMakeNameContainsAndCarModelNameContainsAndCarFuelTypeNameContainsAndCarGearBoxTypeNameContainsAndCarBodyStyleContainsAndCarMileageInKmLessThanEqualAndCarKidsSeatsGreaterThanEqualAndCarAvgRatingGreaterThanEqualAndPriceListPricePerDayBetween(
            Boolean cdw1, LogicalStatus logicalStatus1, LocalDate dateFrom1, LocalDate dateTo1, String pickUpPoint1, String make1, String model1, String fuelType1, String gearBoxType1, String bodyStyle1, Integer maxMileageInKm1, Integer minKidsSeats1, Double minRating1, Double minPricePerDay1, Double maxPricePerDay1,
            Boolean cdw2, LogicalStatus logicalStatus2, LocalDate dateFrom2, String pickUpPoint2, String make2, String model2, String fuelType2, String gearBoxType2, String bodyStyle2, Integer maxMileageInKm2, Integer minKidsSeats2, Double minRating2, Double minPricePerDay2, Double maxPricePerDay2
    );

    // Without CDW available
    List<Advertisement> findByLogicalStatusAndDateFromLessThanEqualAndDateToIsNotNullAndDateToGreaterThanEqualAndPickUpPointContainsIgnoringCaseAndCarMakeNameContainsAndCarModelNameContainsAndCarFuelTypeNameContainsAndCarGearBoxTypeNameContainsAndCarBodyStyleNameContainsAndCarMileageInKmLessThanEqualAndCarKidsSeatsGreaterThanEqualAndCarAvgRatingGreaterThanEqualAndPriceListPricePerDayBetweenOrLogicalStatusAndDateFromLessThanEqualAndDateToIsNullAndPickUpPointContainsIgnoringCaseAndCarMakeNameContainsAndCarModelNameContainsAndCarFuelTypeNameContainsAndCarGearBoxTypeNameContainsAndCarBodyStyleNameContainsAndCarMileageInKmLessThanEqualAndCarKidsSeatsGreaterThanEqualAndCarAvgRatingGreaterThanEqualAndPriceListPricePerDayBetween(
            LogicalStatus logicalStatus1, LocalDate dateFrom1, LocalDate dateTo1, String pickUpPoint1, String make1, String model1, String fuelType1, String gearBoxType1, String bodyStyle1, Integer maxMileageInKm1, Integer minKidsSeats1, Double minRating1, Double minPricePerDay1, Double maxPricePerDay1,
            LogicalStatus logicalStatus2, LocalDate dateFrom2, String pickUpPoint2, String make2, String model2, String fuelType2, String gearBoxType2, String bodyStyle2, Integer maxMileageInKm2, Integer minKidsSeats2, Double minRating2, Double minPricePerDay2, Double maxPricePerDay2
    );

    Advertisement findByMainAppId(Long id);
}
