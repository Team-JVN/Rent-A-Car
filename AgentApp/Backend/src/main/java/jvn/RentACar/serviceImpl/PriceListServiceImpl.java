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
import java.util.ArrayList;
import java.util.List;

@Service
public class PriceListServiceImpl implements PriceListService {

    private PriceListRepository priceListRepository;

    @Autowired
    public PriceListServiceImpl(PriceListRepository priceListRepository) {
        this.priceListRepository = priceListRepository;
    }

    @Override
    public PriceListDTO get(Long id) {
        PriceList priceList = priceListRepository.findOneById(id);
        if (priceList == null) {
            throw new InvalidPriceListDataException("Requested price list does not exist.", HttpStatus.NOT_FOUND);
        }

        return new PriceListDTO(priceList);
    }

    @Override
    public List<PriceListDTO> getAll() {
        return convertToDTO(priceListRepository.findByStatus(LogicalStatus.EXISTING));
    }

    @Override
    public PriceListDTO create(PriceListDTO priceListDTO) {
        PriceList newPriceList = new PriceList(priceListDTO.getPricePerDay(), priceListDTO.getPricePerKm(), priceListDTO.getPriceForCDW());
        return new PriceListDTO(priceListRepository.save(newPriceList));
    }

    @Override
    public PriceListDTO edit(Long id, PriceListDTO priceListDTO) {
        PriceList priceList = getIfEditable(id);

        priceList.setPricePerDay(priceListDTO.getPricePerDay());
        priceList.setPricePerKm(priceListDTO.getPricePerKm());
        priceList.setPriceForCDW(priceListDTO.getPriceForCDW());

        return new PriceListDTO(priceListRepository.save(priceList));
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

    private List<PriceListDTO> convertToDTO(List<PriceList> priceLists) {
        if (priceLists == null || priceLists.isEmpty()) {
            return new ArrayList<>();
        }

        List<PriceListDTO> priceListDTOS = new ArrayList<>();
        for (PriceList priceList : priceLists) {
            priceListDTOS.add(new PriceListDTO(priceList));
        }
        return priceListDTOS;
    }

}
