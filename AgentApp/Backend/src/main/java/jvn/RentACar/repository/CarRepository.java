package jvn.RentACar.repository;

import jvn.RentACar.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findAll();

    Car findOneById(Long id);
}