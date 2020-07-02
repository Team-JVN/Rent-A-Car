package jvn.Cars.repository;

import jvn.Cars.enumeration.LogicalStatus;
import jvn.Cars.model.Car;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findAllByLogicalStatusNotAndOwner(LogicalStatus logicalStatus, Long owner);

    List<Car> findAllByOwner(Long owner);

    Car findOneByIdAndLogicalStatusNotAndOwner(Long id, LogicalStatus logicalStatus, Long owner);

    Car findByIdAndLogicalStatus(Long id, LogicalStatus logicalStatus);

    List<Car> findFirst3ByLogicalStatus(LogicalStatus logicalStatus, Sort sort);

    Car findOneById(Long id);
}