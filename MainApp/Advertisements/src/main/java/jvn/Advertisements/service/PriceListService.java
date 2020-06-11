package jvn.Advertisements.service;


import jvn.Advertisements.dto.request.UserDTO;
import jvn.Advertisements.model.PriceList;

import java.util.List;

public interface PriceListService {
    PriceList get(Long id, Long loggedInUserId);

    List<PriceList> getAll(UserDTO userDTO);

    PriceList create(PriceList priceList, UserDTO userDTO);

    PriceList edit(Long id, PriceList priceList, UserDTO userDTO);

    void delete(Long id, UserDTO userDTO);
}
