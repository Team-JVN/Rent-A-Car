package jvn.Advertisements.service;

import jvn.Advertisements.dto.request.AdvertisementEditDTO;
import jvn.Advertisements.dto.request.UserDTO;
import jvn.Advertisements.enumeration.EditType;
import jvn.Advertisements.model.Advertisement;

import java.time.LocalDate;
import java.util.List;

public interface AdvertisementService {
    Advertisement create(Advertisement createAdvertisementDTO, UserDTO userDTO);

    List<Advertisement> get(List<Long> advertisements);

    Advertisement getOne (Long advertisementId);

    void delete(Long id, Long loggedInUserId);

    Advertisement edit(Long id, Advertisement advertisement, UserDTO userDTO);

    Advertisement editPartial(Long id, AdvertisementEditDTO advertisement, Long loggedInUserId);

    EditType getCarEditType(Long carId);

    Boolean canEditCarPartially(Long carId);

    Boolean canDeleteCar(Long carId);

    boolean checkIfCanDeleteAndDelete(Long id, Long loggedInUser);

    String getAdvertisementEditType(Long id, Long loggedInUser);

    List<Advertisement> getAll(Long loggedInUser);

    boolean checkIfCarIsAvailableForSoap(Long carId, LocalDate dateFrom, LocalDate dateTo);
}

