package jvn.RentACar.serviceImpl;

import jvn.RentACar.enumeration.LogicalStatus;
import jvn.RentACar.exceptionHandler.InvalidPriceListDataException;
import jvn.RentACar.model.PriceList;
import jvn.RentACar.repository.PriceListRepository;
import jvn.RentACar.service.PriceListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceListServiceImpl implements PriceListService {

    private PriceListRepository priceListRepository;

    @Autowired
    public PriceListServiceImpl(PriceListRepository priceListRepository) {
        this.priceListRepository = priceListRepository;
    }

    @Override
    public PriceList get(Long id) {
        PriceList priceList = priceListRepository.findOneByIdAndStatusNot(id, LogicalStatus.DELETED);
        if (priceList == null) {
            throw new InvalidPriceListDataException("Requested price list does not exist.", HttpStatus.NOT_FOUND);
        }
        return priceList;
    }

    @Override
    public List<PriceList> getAll() {
        return priceListRepository.findByStatus(LogicalStatus.EXISTING);
    }

    @Override
    public PriceList create(PriceList priceList) {
        return priceListRepository.save(priceList);
    }

    @Override
    public PriceList edit(Long id, PriceList priceList) {
        PriceList dbPriceList = isEditable(id);
        dbPriceList.setPriceForCDW(priceList.getPriceForCDW());
        dbPriceList.setPricePerDay(priceList.getPricePerDay());
        dbPriceList.setPricePerKm(priceList.getPricePerKm());
        return priceListRepository.save(dbPriceList);
    }

    @Override
    public void delete(Long id) {
        PriceList priceList = isEditable(id);
        priceList.setStatus(LogicalStatus.DELETED);
        priceListRepository.save(priceList);
    }

    private PriceList isEditable(Long id) {
        PriceList priceList = get(id);
        if (!priceList.getAdvertisements().isEmpty()) {
            throw new InvalidPriceListDataException("Price list is used in advertisements, so it can't be edited/deleted.", HttpStatus.FORBIDDEN);
        }
        return priceList;
    }

}
