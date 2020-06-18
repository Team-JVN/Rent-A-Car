package jvn.Advertisements.repository;

import jvn.Advertisements.enumeration.LogicalStatus;
import jvn.Advertisements.model.PriceList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceListRepository extends JpaRepository<PriceList, Long> {

    PriceList findOneByIdAndStatusNotAndOwnerId(Long id, LogicalStatus logicalStatus, Long ownerId);

    List<PriceList> findByStatusAndOwnerId(LogicalStatus status, Long ownerId);

    List<PriceList> findByOwnerId(Long ownerId);

    PriceList findByIdAndStatusNotAndAdvertisementsLogicalStatusNot(Long id, LogicalStatus logicalStatus, LogicalStatus logicalStatusNot);
}
