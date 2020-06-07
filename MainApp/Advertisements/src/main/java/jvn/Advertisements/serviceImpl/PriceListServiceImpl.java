package jvn.Advertisements.serviceImpl;

import jvn.Advertisements.dto.both.PriceListDTO;
import jvn.Advertisements.dto.request.UserDTO;
import jvn.Advertisements.enumeration.LogicalStatus;
import jvn.Advertisements.exceptionHandler.InvalidPriceListDataException;
import jvn.Advertisements.model.PriceList;
import jvn.Advertisements.producer.AdvertisementProducer;
import jvn.Advertisements.repository.PriceListRepository;
import jvn.Advertisements.service.PriceListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceListServiceImpl implements PriceListService {

    private PriceListRepository priceListRepository;

    private AdvertisementProducer advertisementProducer;

    @Autowired
    public PriceListServiceImpl(PriceListRepository priceListRepository, AdvertisementProducer advertisementProducer) {
        this.priceListRepository = priceListRepository;
        this.advertisementProducer = advertisementProducer;
    }

    @Override
    public PriceList get(Long id, Long loggedInUserId) {
        PriceList priceList = priceListRepository.findOneByIdAndStatusNotAndOwnerId(id, LogicalStatus.DELETED, loggedInUserId);
        if (priceList == null) {
            throw new InvalidPriceListDataException("Requested price list does not exist.", HttpStatus.NOT_FOUND);
        }
        return priceList;
    }

    @Override
    public List<PriceList> getAll(UserDTO userDTO) {
        return priceListRepository.findByStatusAndOwnerId(LogicalStatus.EXISTING, userDTO.getId());
    }

    @Override
    public PriceList create(PriceList priceList, UserDTO userDTO) {
        priceList.setOwnerId(userDTO.getId());
        return priceListRepository.save(priceList);
    }

    @Override
    public PriceList edit(Long id, PriceList priceList, UserDTO userDTO) {
        PriceList dbPriceList = get(id, userDTO.getId());
        dbPriceList.setPricePerDay(priceList.getPricePerDay());

        if (dbPriceList.getPricePerKm() != null) {
            dbPriceList.setPricePerKm(priceList.getPricePerKm());
        }
        if (dbPriceList.getPriceForCDW() != null) {
            dbPriceList.setPriceForCDW(priceList.getPriceForCDW());
        }

        dbPriceList = priceListRepository.save(dbPriceList);
        editPriceList(new PriceListDTO(id, dbPriceList.getPricePerDay(), dbPriceList.getPricePerKm(), dbPriceList.getPriceForCDW()));
        return dbPriceList;
    }

    @Override
    public void delete(Long id, UserDTO userDTO) {
        PriceList priceList = get(id, userDTO.getId());

        if (priceListRepository.findByIdAndStatusNotAndAdvertisementsLogicalStatusNot(id, LogicalStatus.DELETED, LogicalStatus.DELETED) != null) {
            throw new InvalidPriceListDataException("Price list is used in advertisements, so it can't be deleted.", HttpStatus.BAD_REQUEST);
        }
        priceList.setStatus(LogicalStatus.DELETED);
        priceListRepository.save(priceList);
    }

    @Async
    public void editPriceList(PriceListDTO priceListDTO) {
        advertisementProducer.sendMessageForSearch(priceListDTO);
    }
}
