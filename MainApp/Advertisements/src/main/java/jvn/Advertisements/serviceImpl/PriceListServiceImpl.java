package jvn.Advertisements.serviceImpl;

import jvn.Advertisements.dto.both.PriceListDTO;
import jvn.Advertisements.dto.message.Log;
import jvn.Advertisements.enumeration.LogicalStatus;
import jvn.Advertisements.exceptionHandler.InvalidAdvertisementDataException;
import jvn.Advertisements.exceptionHandler.InvalidPriceListDataException;
import jvn.Advertisements.model.Advertisement;
import jvn.Advertisements.model.PriceList;
import jvn.Advertisements.producer.AdvertisementProducer;
import jvn.Advertisements.producer.LogProducer;
import jvn.Advertisements.repository.PriceListRepository;
import jvn.Advertisements.service.PriceListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceListServiceImpl implements PriceListService {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private PriceListRepository priceListRepository;

    private AdvertisementProducer advertisementProducer;

    private LogProducer logProducer;

    @Override
    public PriceList get(Long id, Long loggedInUserId) {
        PriceList priceList = priceListRepository.findOneByIdAndStatusNotAndOwnerId(id, LogicalStatus.DELETED, loggedInUserId);
        if (priceList == null) {
            throw new InvalidPriceListDataException("Requested price list does not exist.", HttpStatus.NOT_FOUND);
        }
        return priceList;
    }

    @Override
    public List<PriceList> getAll(Long loggedInUserId) {
        return priceListRepository.findByStatusAndOwnerId(LogicalStatus.EXISTING, loggedInUserId);
    }

    @Override
    public PriceList create(PriceList priceList, Long loggedInUserId) {
        priceList.setOwnerId(loggedInUserId);
        return priceListRepository.save(priceList);
    }

    @Override
    public PriceList edit(Long id, PriceList priceList, Long loggedInUserId) {
        PriceList dbPriceList = get(id, loggedInUserId);
        checkOwner(dbPriceList, loggedInUserId);

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
    public void delete(Long id, Long loggedInUserId) {
        PriceList priceList = get(id, loggedInUserId);
        checkOwner(priceList, loggedInUserId);

        if (priceListRepository.findByIdAndStatusNotAndAdvertisementsLogicalStatusNot(id, LogicalStatus.DELETED, LogicalStatus.DELETED) != null) {
            throw new InvalidPriceListDataException("Price list is used in advertisements, so it can't be deleted.", HttpStatus.BAD_REQUEST);
        }
        priceList.setStatus(LogicalStatus.DELETED);
        priceListRepository.save(priceList);
    }

    @Override
    public boolean checkIfCanDeleteAndDelete(Long id, Long loggedInUserId) {
        PriceList priceList = get(id, loggedInUserId);
        if (priceListRepository.findByIdAndStatusNotAndAdvertisementsLogicalStatusNot(id, LogicalStatus.DELETED, LogicalStatus.DELETED) != null) {
            return false;
        }
        priceList.setStatus(LogicalStatus.DELETED);
        priceListRepository.save(priceList);
        return true;
    }

    @Override
    public List<PriceList> getPriceListsDeletedAndExisting(Long loggedInUserId) {
        return priceListRepository.findByOwnerId(loggedInUserId);
    }

    @Async
    public void editPriceList(PriceListDTO priceListDTO) {
        advertisementProducer.sendMessageForSearch(priceListDTO);
    }

    private void checkOwner(PriceList priceList, Long loggedInUserId) {
        if (!priceList.getOwnerId().equals(loggedInUserId)) {
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CHO", String.format("User %s is not the owner of price list %s", loggedInUserId, priceList.getId())));
            throw new InvalidAdvertisementDataException("You are not the owner of this price list, therefore you cannot edit or delete it.",
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Autowired
    public PriceListServiceImpl(PriceListRepository priceListRepository, AdvertisementProducer advertisementProducer,
                                LogProducer logProducer) {
        this.priceListRepository = priceListRepository;
        this.advertisementProducer = advertisementProducer;
        this.logProducer = logProducer;
    }
}
