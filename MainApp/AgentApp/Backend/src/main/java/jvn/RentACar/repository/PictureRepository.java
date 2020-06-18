package jvn.RentACar.repository;

import jvn.RentACar.model.Picture;
import jvn.RentACar.service.RepositoryWithRefreshMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PictureRepository extends RepositoryWithRefreshMethod<Picture, Long> {

    Picture findByDataAndCarId(String data, Long id);
}
