package jvn.RentACar.service;

import jvn.RentACar.dto.both.PriceListDTO;
import jvn.RentACar.model.PriceList;

import java.util.List;

public interface PriceListService {

    PriceList get(Long id);

    List<PriceList> getAll();

    PriceList create(PriceListDTO priceListDTO);

    PriceList edit(Long id, PriceListDTO priceListDTO);

    void delete(Long id);

}
