package jvn.Cars.repository;

import jvn.Cars.enumeration.LogicalStatus;
import jvn.Cars.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findAllByLogicalStatusNotAndOwner(LogicalStatus logicalStatus,Long owner);

    Car findOneByIdAndLogicalStatusNotAndOwner(Long id, LogicalStatus logicalStatus,Long owner);

//    Car findByIdAndAdvertisementsLogicalStatusAndAdvertisementsDateToEquals(Long id, LogicalStatus logicalStatus, LocalDate date);

//    Car findByIdAndAdvertisementsLogicalStatusAndAdvertisementsDateToGreaterThanEqual(Long id, LogicalStatus logicalStatus, LocalDate date);

}