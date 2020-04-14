package jvn.RentACar.serviceImpl;

import jvn.RentACar.dto.both.PriceListDTO;
import jvn.RentACar.exceptionHandler.InvalidPriceListDataException;
import jvn.RentACar.model.PriceList;
import jvn.RentACar.repository.PriceListRepository;
import jvn.RentACar.service.PriceListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
            throw new InvalidPriceListDataException("Requested price list doesn't exist.", HttpStatus.BAD_REQUEST);
        }

        return new PriceListDTO();
    }

    @Override
    public List<PriceListDTO> getAll() {
        return convertToDTO(priceListRepository.findAll());
    }

    @Override
    public PriceListDTO create(PriceListDTO priceListDTO) {
        PriceList newPriceList = new PriceList(priceListDTO.getPricePerDay(), priceListDTO.getPricePerKm(), priceListDTO.getPriceForCDW());
        return new PriceListDTO(priceListRepository.save(newPriceList));
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
