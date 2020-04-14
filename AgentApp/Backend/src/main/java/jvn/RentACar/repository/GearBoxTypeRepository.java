package jvn.RentACar.repository;

import jvn.RentACar.model.GearBoxType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GearBoxTypeRepository extends JpaRepository<GearBoxType, Long> {

    GearBoxType findByName(String name);

    GearBoxType findByNameAndIdNot(String name, Long id);

    List<GearBoxType> findAll();

    GearBoxType getById(Long id);
}