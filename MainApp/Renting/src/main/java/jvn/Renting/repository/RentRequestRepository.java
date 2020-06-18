package jvn.Renting.repository;

import jvn.Renting.enumeration.RentRequestStatus;
import jvn.Renting.model.RentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RentRequestRepository extends JpaRepository<RentRequest, Long> {

    List<RentRequest> findByRentRequestStatusAndRentInfosDateTimeFromLessThanEqualAndRentInfosDateTimeToGreaterThanAndRentInfosAdvertisement(RentRequestStatus status, LocalDateTime localDate, LocalDateTime localDate1, Long advertisementId);

    List<RentRequest> findByRentRequestStatusAndRentInfosDateTimeFromGreaterThanEqualAndRentInfosDateTimeToLessThanAndRentInfosAdvertisement(RentRequestStatus status, LocalDateTime localDateFrom, LocalDateTime localDateTo, Long advertisementId);

    List<RentRequest> findByRentRequestStatusNotAndRentInfosDateTimeFromLessThanEqualAndRentInfosDateTimeToGreaterThanEqualAndRentInfosAdvertisementOrRentRequestStatusNotAndRentInfosDateTimeFromLessThanEqualAndRentInfosDateTimeToGreaterThanEqualAndRentInfosAdvertisementOrRentRequestStatusNotAndRentInfosDateTimeFromGreaterThanEqualAndRentInfosDateTimeToLessThanEqualAndRentInfosAdvertisement(
            RentRequestStatus status, LocalDateTime localDateFrom, LocalDateTime localDateFrom1, Long advertisement1, RentRequestStatus status1, LocalDateTime localDateTo, LocalDateTime localDateTo1, Long advertisement2, RentRequestStatus status2, LocalDateTime localDateFrom2, LocalDateTime localDateTo2, Long advertisementId3
    );

    List<RentRequest> findByRentInfosAdvertisement(Long id);

    List<RentRequest> findByRentInfosAdvertisementAndRentRequestStatus(Long id, RentRequestStatus rentRequestStatus);

    RentRequest findOneByIdAndCreatedByOrIdAndClient(Long id, Long createdBy, Long id1, Long client);

    List<RentRequest> findByClient(Long id);

    List<RentRequest> findByClientAndRentRequestStatus(Long id, RentRequestStatus status);

    RentRequest findOneByIdAndRentRequestStatus(Long id, RentRequestStatus status);

    List<RentRequest> findByRentInfosAdvertisementAndRentInfosRentRequestRentRequestStatusNot(Long advId, RentRequestStatus status);

    List<RentRequest> findByRentInfosAdvertisementAndRentInfosRentRequestRentRequestStatus(Long advId, RentRequestStatus status);

    List<RentRequest> findByRentInfosAdvertisementAndRentInfosRentRequestRentRequestStatusAndRentInfosDateTimeToGreaterThanEqual(Long advId, RentRequestStatus status, LocalDateTime localDateTime);

    RentRequest findOneById(Long id);

    List<RentRequest> findByRentRequestStatusAndRentInfosRentReportPaidAndClient(RentRequestStatus status, Boolean paid, Long clientId);

    List<RentRequest> findAllByAdvertisementOwner(Long advOwner);

}
