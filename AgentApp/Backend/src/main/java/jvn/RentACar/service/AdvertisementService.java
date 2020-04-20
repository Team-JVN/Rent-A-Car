package jvn.RentACar.service;

import jvn.RentACar.dto.response.AdvertisementWithPicturesDTO;
import jvn.RentACar.model.Advertisement;

import java.util.List;

public interface AdvertisementService {
    Advertisement create(Advertisement createAdvertisementDTO);

    List<AdvertisementWithPicturesDTO> getAll();
}
