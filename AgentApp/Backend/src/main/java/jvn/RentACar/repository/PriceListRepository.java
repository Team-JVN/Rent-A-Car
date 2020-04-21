package jvn.RentACar.repository;

import jvn.RentACar.enumeration.LogicalStatus;
import jvn.RentACar.model.PriceList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceListRepository extends JpaRepository<PriceList, Long> {

    PriceList findOneByIdAndStatusNot(Long id, LogicalStatus logicalStatus);

    List<PriceList> findByStatus(LogicalStatus status);
}
