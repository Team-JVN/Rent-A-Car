package jvn.RentACar.repository;

import jvn.RentACar.model.Make;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MakeRepository extends JpaRepository<Make, Long> {

    Make findByName(String name);

    Make findByNameAndIdNot(String name, Long id);

    List<Make> findAll();

    Make findOneById(Long id);
}

