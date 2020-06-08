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

    List<RentRequest> findByRentRequestStatusAndRentInfosDateTimeFromLessThanEqualAndRentInfosDateTimeToGreaterThanAndRentInfosAdvertisementId(RentRequestStatus status, LocalDateTime localDate, LocalDateTime localDate1, Long advertisementId);

    List<RentRequest> findByRentRequestStatusAndRentInfosDateTimeFromGreaterThanEqualAndRentInfosDateTimeToLessThanAndRentInfosAdvertisementId(RentRequestStatus status, LocalDateTime localDateFrom, LocalDateTime localDateTo, Long advertisementId);


    List<RentRequest> findByRentInfosAdvertisementIdAndRentInfosRentRequestRentRequestStatus(Long advId, RentRequestStatus status);

    List<RentRequest> findByRentRequestStatusNotAndRentInfosDateTimeFromLessThanEqualAndRentInfosDateTimeToGreaterThanEqualAndRentInfosAdvertisementIdOrRentRequestStatusNotAndRentInfosDateTimeFromLessThanEqualAndRentInfosDateTimeToGreaterThanEqualAndRentInfosAdvertisementIdOrRentRequestStatusNotAndRentInfosDateTimeFromGreaterThanEqualAndRentInfosDateTimeToLessThanEqualAndRentInfosAdvertisementId(
            RentRequestStatus status, LocalDateTime localDateFrom, LocalDateTime localDateFrom1, Long advertisement1, RentRequestStatus status1, LocalDateTime localDateTo, LocalDateTime localDateTo1, Long advertisement2, RentRequestStatus status2, LocalDateTime localDateFrom2, LocalDateTime localDateTo2, Long advertisementId3
    );

}

