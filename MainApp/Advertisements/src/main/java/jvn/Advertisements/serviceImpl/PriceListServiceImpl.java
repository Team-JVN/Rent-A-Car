package jvn.Advertisements.serviceImpl;

import jvn.Advertisements.dto.request.UserDTO;
import jvn.Advertisements.enumeration.LogicalStatus;
import jvn.Advertisements.exceptionHandler.InvalidPriceListDataException;
import jvn.Advertisements.model.PriceList;
import jvn.Advertisements.repository.PriceListRepository;
import jvn.Advertisements.service.PriceListService;
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
        dbPriceList.setPriceForCDW(priceList.getPriceForCDW());
        dbPriceList.setPricePerDay(priceList.getPricePerDay());
        dbPriceList.setPricePerKm(priceList.getPricePerKm());
        return priceListRepository.save(dbPriceList);
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
}
