package jvn.RentACar.repository;

import jvn.RentACar.enumeration.LogicalStatus;
import jvn.RentACar.model.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

    Advertisement findOneById(Long id);

    List<Advertisement> findAllByLogicalStatusNot(LogicalStatus logicalStatus);
}
