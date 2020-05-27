package jvn.Advertisements.service;

import jvn.Advertisements.model.Advertisement;

import java.util.List;

public interface AdvertisementService {
    Advertisement create(Advertisement createAdvertisementDTO);

    List<Advertisement> getAll(String status);
}

