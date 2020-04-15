package jvn.RentACar.repository;

import jvn.RentACar.enumeration.LogicalStatus;
import jvn.RentACar.model.Advertisement;
import jvn.RentACar.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findAllByLogicalStatusNot(LogicalStatus logicalStatus);

    Car findOneByIdAndLogicalStatusNot(Long id, LogicalStatus logicalStatus);

    List<Advertisement> findByIdAndAdvertisementsDateToAfter(Long id, LocalDate localDate);
}