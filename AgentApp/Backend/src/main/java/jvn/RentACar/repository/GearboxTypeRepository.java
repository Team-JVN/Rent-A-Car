package jvn.RentACar.repository;

import jvn.RentACar.model.GearboxType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GearboxTypeRepository extends JpaRepository<GearboxType, Long> {

    GearboxType findByName(String name);

    GearboxType findByNameAndIdNot(String name, Long id);

    List<GearboxType> findAll();

    GearboxType findOneById(Long id);
}