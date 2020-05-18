package jvn.RentACar.repository;

import jvn.RentACar.model.Picture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PictureRepository extends JpaRepository<Picture, Long> {

    Picture findByDataAndCarId(String data, Long id);
}
