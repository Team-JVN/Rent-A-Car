package jvn.Renting.repository;

import jvn.Renting.enumeration.RentRequestStatus;
import jvn.Renting.model.RentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentInfoRepository extends JpaRepository<RentInfo, Long> {

    RentInfo findByIdAndRentRequestIdAndRentRequestRentRequestStatusAndRentRequestClient(
            Long rentInfoId, Long rentRequestId, RentRequestStatus status, Long client
    );

    RentInfo findByIdAndRentRequestId(Long rentInfoId, Long rentRequestId);

    RentInfo findOneById(Long id);
}
