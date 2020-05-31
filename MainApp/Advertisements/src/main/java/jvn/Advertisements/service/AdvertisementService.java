package jvn.Advertisements.service;

import jvn.Advertisements.dto.request.UserDTO;
import jvn.Advertisements.model.Advertisement;

import java.util.List;

public interface AdvertisementService {
    Advertisement create(Advertisement createAdvertisementDTO, UserDTO userDTO);
}

