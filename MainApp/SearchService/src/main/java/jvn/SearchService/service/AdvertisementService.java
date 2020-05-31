package jvn.SearchService.service;

import jvn.SearchService.dto.SearchParamsDTO;
import jvn.SearchService.model.Advertisement;

import java.util.List;

public interface AdvertisementService {

    List<Advertisement> getAll();

    List<Advertisement> getAllMy(String status,Long id);

    List<Advertisement> searchAdvertisements(SearchParamsDTO searchParamsDTO);
}

