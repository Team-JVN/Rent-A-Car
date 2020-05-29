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
    List<Advertisement> findByCDWAndLogicalStatusAndDateFromLessThanEqualAndDateToGreaterThanEqualAndKilometresLimitGreaterThanEqualAndPickUpPointContainsIgnoringCaseAndCarMakeAndCarModelAndCarFuelTypeAndCarGearBoxTypeAndCarBodyStyleAndCarMileageInKmLessThanEqualAndCarKidsSeatsGreaterThanEqualAndCarAvgRatingGreaterThanEqualAndPriceListPricePerDayBetween(
            Boolean cdw, LogicalStatus logicalStatus, LocalDate dateFrom, LocalDate dateTo, Integer minKilometresLimit, String pickUpPoint, String make, String model, String fuelType, String gearBoxType, String bodyStyle, Integer maxMileageInKm, Integer minKidsSeats, Double minRating, Double minPricePerDay, Double maxPricePerDay
    );

    // Without CDW available
    List<Advertisement> findByLogicalStatusAndDateFromLessThanEqualAndDateToGreaterThanEqualAndKilometresLimitGreaterThanEqualAndPickUpPointContainsIgnoringCaseAndCarMakeAndCarModelAndCarFuelTypeAndCarGearBoxTypeAndCarBodyStyleAndCarMileageInKmLessThanEqualAndCarKidsSeatsGreaterThanEqualAndCarAvgRatingGreaterThanEqualAndPriceListPricePerDayBetween(
            LogicalStatus logicalStatus, LocalDate dateFrom, LocalDate dateTo, Integer minKilometresLimit, String pickUpPoint, String make, String model, String fuelType, String gearBoxType, String bodyStyle, Integer maxMileageInKm, Integer minKidsSeats, Double minRating, Double minPricePerDay, Double maxPricePerDay
    );

}