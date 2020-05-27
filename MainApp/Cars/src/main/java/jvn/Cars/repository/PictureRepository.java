package jvn.Cars.repository;

import jvn.Cars.model.Picture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PictureRepository extends JpaRepository<Picture, Long> {

    Picture findByDataAndCarId(String data, Long id);
}
