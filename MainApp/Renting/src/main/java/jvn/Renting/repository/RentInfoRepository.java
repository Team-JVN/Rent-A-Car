package jvn.Renting.repository;

import jvn.Renting.enumeration.RentRequestStatus;
import jvn.Renting.model.RentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentInfoRepository extends JpaRepository<RentInfo, Long> {

    RentInfo findByIdAndRentRequestIdAndRentRequestRentRequestStatusAndRentReportPaidAndRentRequestClient(
            Long rentInfoId, Long rentRequestId, RentRequestStatus status, boolean paid, Long client
    );
}
