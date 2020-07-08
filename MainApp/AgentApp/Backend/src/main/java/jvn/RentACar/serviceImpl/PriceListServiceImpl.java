package jvn.RentACar.serviceImpl;

import jvn.RentACar.client.PriceListClient;
import jvn.RentACar.dto.soap.pricelist.DeletePriceListDetailsResponse;
import jvn.RentACar.dto.soap.pricelist.GetAllPriceListDetailsResponse;
import jvn.RentACar.dto.soap.pricelist.GetPriceListDetailsResponse;
import jvn.RentACar.dto.soap.pricelist.PriceListDetails;
import jvn.RentACar.enumeration.LogicalStatus;
import jvn.RentACar.exceptionHandler.InvalidPriceListDataException;
import jvn.RentACar.mapper.PriceListDetailsMapper;
import jvn.RentACar.model.Log;
import jvn.RentACar.model.PriceList;
import jvn.RentACar.repository.PriceListRepository;
import jvn.RentACar.service.LogService;
import jvn.RentACar.service.PriceListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PriceListServiceImpl implements PriceListService {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private PriceListRepository priceListRepository;

    private PriceListClient priceListClient;

    private PriceListDetailsMapper priceListDetailsMapper;

    private LogService logService;

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
        synchronizePriceLists();
        return priceListRepository.findByStatus(LogicalStatus.EXISTING);
    }

    @Override
    public PriceList create(PriceList priceList) {
        GetPriceListDetailsResponse response = priceListClient.createOrEdit(priceList);
        PriceListDetails priceListDetails = response.getPriceListDetails();
        if (priceListDetails != null && priceListDetails.getId() != null) {
            priceList.setMainAppId(priceListDetails.getId());
        }
        return priceListRepository.save(priceList);
    }

    @Override
    public PriceList edit(Long id, PriceList priceList) {
        PriceList dbPriceList = get(id);
        dbPriceList.setPricePerDay(priceList.getPricePerDay());

        if (dbPriceList.getPricePerKm() != null) {
            dbPriceList.setPricePerKm(priceList.getPricePerKm());
        }
        if (dbPriceList.getPriceForCDW() != null) {
            dbPriceList.setPriceForCDW(priceList.getPriceForCDW());
        }

        GetPriceListDetailsResponse response = priceListClient.createOrEdit(dbPriceList);
        PriceListDetails priceListDetails = response.getPriceListDetails();
        if (priceListDetails != null && priceListDetails.getId() != null) {
            dbPriceList.setMainAppId(priceListDetails.getId());
        }

        return priceListRepository.save(dbPriceList);
    }

    @Override
    public void delete(Long id) {
        PriceList priceList = get(id);

        DeletePriceListDetailsResponse response = priceListClient.checkAndDeleteIfCan(priceList);
        if (response == null || !response.isCanDelete()) {
            throw new InvalidPriceListDataException("Price list is used in advertisements, so it can't be deleted.", HttpStatus.BAD_REQUEST);
        }
        priceList.setStatus(LogicalStatus.DELETED);
        priceListRepository.save(priceList);
    }

    @Override
    public PriceList getByMainAppId(Long mainAppId) {
        return priceListRepository.findByMainAppId(mainAppId);
    }


    @Scheduled(cron = "0 0 0/3 * * ?")
    private void synchronizePriceLists() {
        GetAllPriceListDetailsResponse response = priceListClient.getAll();
        List<PriceListDetails> priceListDetails = response.getPriceListDetails();
        if (priceListDetails == null) {
            return;
        }
        List<PriceList> priceLists = priceListDetails.stream().map(priceListDetailsMapper::toEntity).
                collect(Collectors.toList());
        for (PriceList priceList : priceLists) {
            PriceList dbPriceList = priceListRepository.findByMainAppId(priceList.getMainAppId());
            if (dbPriceList != null) {
                dbPriceList.setPricePerKm(priceList.getPricePerKm());
                dbPriceList.setPricePerDay(priceList.getPricePerDay());
                dbPriceList.setPriceForCDW(priceList.getPriceForCDW());
                dbPriceList.setStatus(priceList.getStatus());
                priceListRepository.save(dbPriceList);
            } else {
                priceListRepository.save(priceList);
            }
        }
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "SYN", "[SOAP] Price lists are successfully synchronized"));
    }

    @Autowired
    public PriceListServiceImpl(PriceListRepository priceListRepository, PriceListClient priceListClient,
                                PriceListDetailsMapper priceListDetailsMapper, LogService logService) {
        this.priceListRepository = priceListRepository;
        this.priceListClient = priceListClient;
        this.priceListDetailsMapper = priceListDetailsMapper;
        this.logService = logService;
    }
}
