package jvn.SearchService.repository;

import jvn.SearchService.enumeration.LogicalStatus;
import jvn.SearchService.model.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

    List<Advertisement> findByLogicalStatus(LogicalStatus logicalStatus);

    // With CDW available
    List<Advertisement> findByCDWAndLogicalStatusAndDateFromLessThanEqualAndDateToIsNotNullAndDateToGreaterThanEqualAndPickUpPointContainsIgnoringCaseAndCarMakeContainsAndCarModelContainsAndCarFuelTypeContainsAndCarGearBoxTypeContainsAndCarBodyStyleContainsAndCarMileageInKmLessThanEqualAndCarKidsSeatsGreaterThanEqualAndCarAvgRatingGreaterThanEqualAndPriceListPricePerDayBetweenOrCDWAndLogicalStatusAndDateFromLessThanEqualAndDateToIsNullAndPickUpPointContainsIgnoringCaseAndCarMakeContainsAndCarModelContainsAndCarFuelTypeContainsAndCarGearBoxTypeContainsAndCarBodyStyleContainsAndCarMileageInKmLessThanEqualAndCarKidsSeatsGreaterThanEqualAndCarAvgRatingGreaterThanEqualAndPriceListPricePerDayBetween(
            Boolean cdw1, LogicalStatus logicalStatus1, LocalDate dateFrom1, LocalDate dateTo1, String pickUpPoint1, String make1, String model1, String fuelType1, String gearBoxType1, String bodyStyle1, Integer maxMileageInKm1, Integer minKidsSeats1, Double minRating1, Double minPricePerDay1, Double maxPricePerDay1,
            Boolean cdw2, LogicalStatus logicalStatus2, LocalDate dateFrom2, String pickUpPoint2, String make2, String model2, String fuelType2, String gearBoxType2, String bodyStyle2, Integer maxMileageInKm2, Integer minKidsSeats2, Double minRating2, Double minPricePerDay2, Double maxPricePerDay2
    );

    // Without CDW available
    List<Advertisement> findByLogicalStatusAndDateFromLessThanEqualAndDateToIsNotNullAndDateToGreaterThanEqualAndPickUpPointContainsIgnoringCaseAndCarMakeContainsAndCarModelContainsAndCarFuelTypeContainsAndCarGearBoxTypeContainsAndCarBodyStyleContainsAndCarMileageInKmLessThanEqualAndCarKidsSeatsGreaterThanEqualAndCarAvgRatingGreaterThanEqualAndPriceListPricePerDayBetweenOrLogicalStatusAndDateFromLessThanEqualAndDateToIsNullAndPickUpPointContainsIgnoringCaseAndCarMakeContainsAndCarModelContainsAndCarFuelTypeContainsAndCarGearBoxTypeContainsAndCarBodyStyleContainsAndCarMileageInKmLessThanEqualAndCarKidsSeatsGreaterThanEqualAndCarAvgRatingGreaterThanEqualAndPriceListPricePerDayBetween(
            LogicalStatus logicalStatus1, LocalDate dateFrom1, LocalDate dateTo1, String pickUpPoint1, String make1, String model1, String fuelType1, String gearBoxType1, String bodyStyle1, Integer maxMileageInKm1, Integer minKidsSeats1, Double minRating1, Double minPricePerDay1, Double maxPricePerDay1,
            LogicalStatus logicalStatus2, LocalDate dateFrom2, String pickUpPoint2, String make2, String model2, String fuelType2, String gearBoxType2, String bodyStyle2, Integer maxMileageInKm2, Integer minKidsSeats2, Double minRating2, Double minPricePerDay2, Double maxPricePerDay2
    );

}