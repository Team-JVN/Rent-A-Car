package jvn.RentACar.service;

import jvn.RentACar.model.Advertisement;

public interface AdvertisementService {
    Advertisement create(Advertisement createAdvertisementDTO);

    Advertisement edit(Long id, Advertisement advertisement);

    void delete(Long id);

    Advertisement get(Long id);
}
