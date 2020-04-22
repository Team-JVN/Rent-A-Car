package jvn.RentACar.service;

import jvn.RentACar.dto.response.AdvertisementWithPicturesDTO;
import jvn.RentACar.model.Advertisement;

import java.util.List;

public interface AdvertisementService {
    Advertisement create(Advertisement createAdvertisementDTO);

    Advertisement edit(Long id, Advertisement advertisement);

    void delete(Long id);

    Advertisement get(Long id);

    List<AdvertisementWithPicturesDTO> getAll(String status);
}
