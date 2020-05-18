package jvn.RentACar.service;

import jvn.RentACar.model.PriceList;

import java.util.List;

public interface PriceListService {

    PriceList get(Long id);

    List<PriceList> getAll();

    PriceList create(PriceList priceList);

    PriceList edit(Long id, PriceList priceList);

    void delete(Long id);

}
