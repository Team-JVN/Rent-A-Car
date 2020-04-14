package jvn.RentACar.repository;

import jvn.RentACar.model.BodyStyle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BodyStyleRepository extends JpaRepository<BodyStyle, Long> {

    BodyStyle findByName(String name);

    BodyStyle findByNameAndIdNot(String name, Long id);

    List<BodyStyle> findAll();

    BodyStyle getById(Long id);
}
