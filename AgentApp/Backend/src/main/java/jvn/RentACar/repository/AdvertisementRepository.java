package jvn.RentACar.repository;

import jvn.RentACar.enumeration.LogicalStatus;
import jvn.RentACar.enumeration.RentRequestStatus;
import jvn.RentACar.model.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

    Advertisement findByIdAndLogicalStatus(Long id, LogicalStatus logicalStatus);

    List<Advertisement> findAllByLogicalStatusNot(LogicalStatus logicalStatus);

    List<Advertisement> findByCarIdAndLogicalStatus(Long id, LogicalStatus status);

    Advertisement findByIdAndRentInfosRentRequestRentRequestStatusNotAndLogicalStatus(Long id, RentRequestStatus status, LogicalStatus logicalStatus);
}
