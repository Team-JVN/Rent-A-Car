package jvn.Advertisements.repository;

import jvn.Advertisements.enumeration.LogicalStatus;
import jvn.Advertisements.model.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

    List<Advertisement> findByCarAndLogicalStatusNotAndDateToEquals(Long id, LogicalStatus status, LocalDate localDate);

    List<Advertisement> findByCarAndLogicalStatusNotAndDateToGreaterThanEqual(Long id, LogicalStatus status, LocalDate localDateFrom);

    List<Advertisement> findByCarAndLogicalStatusNotAndDateFromLessThanEqualAndDateToGreaterThanEqual(Long id, LogicalStatus status, LocalDate localDateTo, LocalDate localDateFrom);

    List<Advertisement> findByOwnerAndDateToGreaterThanEqualAndLogicalStatus(Long id, LocalDate localDate, LogicalStatus status);

    List<Advertisement> findByIdInAndLogicalStatus(Collection<Long> id, LogicalStatus logicalStatus);

    Advertisement findByIdAndLogicalStatus(Long id, LogicalStatus status);

    List<Advertisement> findByCarAndLogicalStatusAndDateToGreaterThanEqualOrCarAndLogicalStatusAndDateToEquals(Long carId1, LogicalStatus logicalStatus1, LocalDate currentDate, Long carId2, LogicalStatus logicalStatus2, LocalDate nullDate);
}