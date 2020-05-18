package jvn.RentACar.repository;

import jvn.RentACar.enumeration.RentRequestStatus;
import jvn.RentACar.model.RentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RentRequestRepository extends JpaRepository<RentRequest, Long> {

    List<RentRequest> findAll();

    RentRequest findOneById(Long id);

    List<RentRequest> findByClientEmail(String email);

    List<RentRequest> findByClientEmailAndRentRequestStatus(String email, RentRequestStatus rentRequestStatus);

    List<RentRequest> findByRentInfosAdvertisementIdAndRentRequestStatus(Long id, RentRequestStatus rentRequestStatus);

    List<RentRequest> findByRentInfosAdvertisementId(Long id);

    List<RentRequest> findByRentRequestStatusAndRentInfosDateTimeFromLessThanEqualAndRentInfosDateTimeToGreaterThanEqual(RentRequestStatus status, LocalDateTime localDate, LocalDateTime localDate1);

    List<RentRequest> findByRentRequestStatusAndRentInfosDateTimeFromGreaterThanEqualAndRentInfosDateTimeToLessThanEqual(RentRequestStatus status, LocalDateTime localDateFrom, LocalDateTime localDateTo);

}

