package jvn.RentACar.serviceImpl;

import jvn.RentACar.dto.both.PriceListDTO;
import jvn.RentACar.enumeration.LogicalStatus;
import jvn.RentACar.exceptionHandler.InvalidPriceListDataException;
import jvn.RentACar.model.Advertisement;
import jvn.RentACar.model.PriceList;
import jvn.RentACar.repository.PriceListRepository;
import jvn.RentACar.service.PriceListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        PriceList priceList = priceListRepository.findOneById(id);
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
    public PriceList create(PriceListDTO priceListDTO) {
        PriceList newPriceList = new PriceList(priceListDTO.getPricePerDay(), priceListDTO.getPricePerKm(), priceListDTO.getPriceForCDW());
        return priceListRepository.save(newPriceList);
    }

    @Override
    public PriceList edit(Long id, PriceListDTO priceListDTO) {
        PriceList priceList = getIfEditable(id);

        priceList.setPricePerDay(priceListDTO.getPricePerDay());
        priceList.setPricePerKm(priceListDTO.getPricePerKm());
        priceList.setPriceForCDW(priceListDTO.getPriceForCDW());

        return priceListRepository.save(priceList);
    }

    @Override
    public void delete(Long id) {
        PriceList priceList = priceListRepository.findOneById(id);
        if (priceList == null) {
            return;
        }

        for (Advertisement ad : priceList.getAdvertisements()) {
            if (!ad.getDateTo().isBefore(LocalDate.now())) {
                throw new InvalidPriceListDataException("Price list is in use and therefore can not be deleted.", HttpStatus.FORBIDDEN);
            }
        }
        priceList.setStatus(LogicalStatus.DELETED);
        priceListRepository.save(priceList);
    }

    private PriceList getIfEditable(Long id) {
        PriceList priceList = priceListRepository.findOneById(id);
        if (priceList == null) {
            throw new InvalidPriceListDataException("Price list for edit does not exist.", HttpStatus.NOT_FOUND);
        }

        if (priceList.getAdvertisements().isEmpty()) {
            return priceList;
        }

        throw new InvalidPriceListDataException("Price list is used in advertisements, so it can't be edited.", HttpStatus.FORBIDDEN);
    }

}
