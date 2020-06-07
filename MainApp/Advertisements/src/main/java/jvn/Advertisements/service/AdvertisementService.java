package jvn.Advertisements.service;

import jvn.Advertisements.dto.request.AdvertisementEditDTO;
import jvn.Advertisements.dto.request.UserDTO;
import jvn.Advertisements.enumeration.EditType;
import jvn.Advertisements.model.Advertisement;

import java.util.List;

public interface AdvertisementService {
    Advertisement create(Advertisement createAdvertisementDTO, UserDTO userDTO, String jwtToken, String user);

    List<Advertisement> get(List<Long> advertisements);

    void delete(Long id, Long loggedInUserId, String jwtToken, String user);

    Advertisement edit(Long id, Advertisement advertisement, Long loggedInUserId, String jwtToken, String user, UserDTO userDTO);

    Advertisement editPartial(Long id, AdvertisementEditDTO advertisement, Long loggedInUserId);

    EditType getCarEditType(Long carId);

    Boolean canEditCarPartially(Long carId, String jwtToken, String user);

    Boolean canDeleteCar(Long carId);
}

