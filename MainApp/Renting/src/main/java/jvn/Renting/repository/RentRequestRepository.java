package jvn.Renting.repository;

import jvn.Renting.enumeration.RentRequestStatus;
import jvn.Renting.model.RentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RentRequestRepository extends JpaRepository<RentRequest, Long> {

    List<RentRequest> findByRentRequestStatusAndRentInfosDateTimeFromLessThanEqualAndRentInfosDateTimeToGreaterThanEqual(RentRequestStatus status, LocalDateTime localDate, LocalDateTime localDate1);

    List<RentRequest> findByRentRequestStatusAndRentInfosDateTimeFromGreaterThanEqualAndRentInfosDateTimeToLessThanEqual(RentRequestStatus status, LocalDateTime localDateFrom, LocalDateTime localDateTo);
}
