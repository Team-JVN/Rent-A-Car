package jvn.RentACar.repository;

import jvn.RentACar.enumeration.LogicalStatus;
import jvn.RentACar.model.Car;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findAllByLogicalStatusNot(LogicalStatus logicalStatus);

    Car findOneByIdAndLogicalStatusNot(Long id, LogicalStatus logicalStatus);

    Car findByIdAndAdvertisementsLogicalStatusAndAdvertisementsDateToEquals(Long id, LogicalStatus logicalStatus, LocalDate date);

    Car findByIdAndAdvertisementsLogicalStatusAndAdvertisementsDateToGreaterThanEqual(Long id, LogicalStatus logicalStatus, LocalDate date);

    List<Car> findFirst3ByLogicalStatus(LogicalStatus logicalStatus, Sort sort);
}