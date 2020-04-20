package jvn.RentACar.repository;

import jvn.RentACar.enumeration.LogicalStatus;
import jvn.RentACar.model.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

    Advertisement findOneById(Long id);

    List<Advertisement> findAllByLogicalStatusNot(LogicalStatus logicalStatus);
}
