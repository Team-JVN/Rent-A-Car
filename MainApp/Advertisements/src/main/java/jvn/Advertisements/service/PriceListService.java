package jvn.Advertisements.service;


import jvn.Advertisements.dto.request.UserDTO;
import jvn.Advertisements.model.PriceList;

import java.util.List;

public interface PriceListService {
    PriceList get(Long id, Long loggedInUserId);

    List<PriceList> getAll(Long loggedInUserId);

    PriceList create(PriceList priceList, Long loggedInUserId);

    PriceList edit(Long id, PriceList priceList, Long loggedInUserId);

    void delete(Long id,Long loggedInUserId);
}
