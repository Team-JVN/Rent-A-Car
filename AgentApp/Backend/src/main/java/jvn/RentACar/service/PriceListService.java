package jvn.RentACar.service;

import jvn.RentACar.dto.both.PriceListDTO;

import java.util.List;

public interface PriceListService {

    PriceListDTO get(Long id);

    List<PriceListDTO> getAll();

    PriceListDTO create(PriceListDTO priceListDTO);

    PriceListDTO edit(Long id, PriceListDTO priceListDTO);

    void delete(Long id);

}
