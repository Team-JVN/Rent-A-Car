package jvn.Advertisements.service;


import jvn.Advertisements.model.PriceList;
import java.util.List;

public interface PriceListService {
    PriceList get(Long id);

    List<PriceList> getAll();

    PriceList create(PriceList priceList);

    PriceList edit(Long id, PriceList priceList);

    void delete(Long id);
}
