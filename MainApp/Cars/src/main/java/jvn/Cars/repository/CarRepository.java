package jvn.Cars.repository;

import jvn.Cars.enumeration.LogicalStatus;
import jvn.Cars.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findAllByLogicalStatusNot(LogicalStatus logicalStatus);

    Car findOneByIdAndLogicalStatusNot(Long id, LogicalStatus logicalStatus);

//    Car findByIdAndAdvertisementsLogicalStatusAndAdvertisementsDateToEquals(Long id, LogicalStatus logicalStatus, LocalDate date);

//    Car findByIdAndAdvertisementsLogicalStatusAndAdvertisementsDateToGreaterThanEqual(Long id, LogicalStatus logicalStatus, LocalDate date);

}