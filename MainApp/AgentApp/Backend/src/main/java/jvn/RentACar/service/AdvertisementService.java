package jvn.RentACar.service;

import jvn.RentACar.dto.request.AdvertisementEditDTO;
import jvn.RentACar.dto.response.SearchParamsDTO;
import jvn.RentACar.enumeration.EditType;
import jvn.RentACar.model.Advertisement;

import java.util.List;

public interface AdvertisementService {
    Advertisement create(Advertisement createAdvertisementDTO);

    Advertisement edit(Long id, Advertisement advertisement);

    Advertisement editPartial(Long id, AdvertisementEditDTO advertisement);

    void delete(Long id);

    Advertisement get(Long id);

    EditType getEditType(Long id);

    List<Advertisement> getAll(String status);

    List<Advertisement> searchAdvertisements(SearchParamsDTO searchParamsDTO);
}
