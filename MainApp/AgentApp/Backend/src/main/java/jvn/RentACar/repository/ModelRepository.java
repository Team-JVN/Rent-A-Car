package jvn.RentACar.repository;

import jvn.RentACar.model.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {

    Model findByNameAndMakeId(String name, Long id);

    Model findByNameAndIdNotAndMakeId(String name, Long modelId, Long makeId);

    List<Model> findByMakeIdOrderByNameAsc(Long id);

    Model findOneByIdAndMakeId(Long id, Long makeId);
}
