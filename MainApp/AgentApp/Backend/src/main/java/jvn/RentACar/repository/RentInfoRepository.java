package jvn.RentACar.repository;

import jvn.RentACar.enumeration.RentRequestStatus;
import jvn.RentACar.model.RentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentInfoRepository extends JpaRepository<RentInfo, Long> {
    List<RentInfo> findByAdvertisementCarIdAndRentRequestRentRequestStatusOrderByDateTimeToDesc(Long carId, RentRequestStatus rentRequestStatus);

    RentInfo findOneById(Long id);

    RentInfo findByIdAndRentRequestIdAndRentRequestRentRequestStatusAndRentReportPaidAndRentRequestClientId(
            Long rentInfoId, Long rentRequestId, RentRequestStatus status, boolean paid, Long client
    );
}
