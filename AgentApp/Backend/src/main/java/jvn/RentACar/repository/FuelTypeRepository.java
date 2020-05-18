package jvn.RentACar.repository;

import jvn.RentACar.model.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FuelTypeRepository extends JpaRepository<FuelType, Long> {

    FuelType findByName(String name);

    FuelType findByNameAndIdNot(String name, Long id);

    List<FuelType> findAll();

    FuelType findOneById(Long id);

}
