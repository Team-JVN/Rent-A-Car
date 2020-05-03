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

    List<Advertisement> findAllByLogicalStatusNot(LogicalStatus logicalStatus);

    List<Advertisement> findAllByLogicalStatusNotAndDateToEqualsOrLogicalStatusNotAndDateToGreaterThan(LogicalStatus logicalStatus, LocalDate localDate, LogicalStatus logicalStatus1,
                                                                                                       LocalDate localDateToNow);

    List<Advertisement> findAllByLogicalStatusNotAndDateToLessThanEqual(LogicalStatus logicalStatus1, LocalDate localDateToNow);

    Advertisement findByIdAndRentInfosRentRequestRentRequestStatusNotAndLogicalStatus(Long id, RentRequestStatus status, LogicalStatus logicalStatus);

    List<Advertisement> findByCarIdAndLogicalStatusNotAndDateToEquals(Long id, LogicalStatus status, LocalDate localDate);

    List<Advertisement> findByCarIdAndLogicalStatusNotAndDateToGreaterThanEqual(Long id, LogicalStatus status, LocalDate localDateFrom);

    List<Advertisement> findByCarIdAndLogicalStatusNotAndDateFromLessThanEqualAndDateToGreaterThanEqual(Long id, LogicalStatus status, LocalDate localDateTo, LocalDate localDateFrom);

    Advertisement findByIdAndLogicalStatusAndRentInfosRentRequestRentRequestStatusAndRentInfosDateTimeToGreaterThanEqual(Long id, LogicalStatus logicalStatus, RentRequestStatus status, LocalDateTime localDateTime);
}
