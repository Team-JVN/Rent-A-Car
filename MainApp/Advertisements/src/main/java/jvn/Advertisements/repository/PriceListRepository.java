package jvn.Advertisements.repository;

import jvn.Advertisements.enumeration.LogicalStatus;
import jvn.Advertisements.model.PriceList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceListRepository extends JpaRepository<PriceList, Long> {

    PriceList findOneByIdAndStatusNot(Long id, LogicalStatus logicalStatus);

    List<PriceList> findByStatus(LogicalStatus status);

    PriceList findByIdAndStatusNotAndAdvertisementsLogicalStatusNot(Long id, LogicalStatus logicalStatus, LogicalStatus logicalStatusNot);
}
