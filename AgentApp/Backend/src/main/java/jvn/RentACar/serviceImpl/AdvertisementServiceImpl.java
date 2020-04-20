package jvn.RentACar.serviceImpl;

import jvn.RentACar.enumeration.LogicalStatus;
import jvn.RentACar.exceptionHandler.InvalidAdvertisementDataException;
import jvn.RentACar.model.Advertisement;
import jvn.RentACar.model.PriceList;
import jvn.RentACar.repository.AdvertisementRepository;
import jvn.RentACar.service.AdvertisementService;
import jvn.RentACar.service.CarService;
import jvn.RentACar.service.PriceListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AdvertisementServiceImpl implements AdvertisementService {

    private CarService carService;

    private PriceListService priceListService;

    private AdvertisementRepository advertisementRepository;

    @Override
    public Advertisement create(Advertisement createAdvertisementDTO) {
        //TODO: CHECK IF CAR IS AVAILABLE !!!!
        createAdvertisementDTO.setCar(carService.get(createAdvertisementDTO.getCar().getId()));
        createAdvertisementDTO.setPriceList(priceListService.get(createAdvertisementDTO.getPriceList().getId()));
        PriceList priceList = createAdvertisementDTO.getPriceList();
        if (priceList.getPricePerKm() != null && createAdvertisementDTO.getKilometresLimit() == null) {
            throw new InvalidAdvertisementDataException("You have to set kilometres limit.", HttpStatus.BAD_REQUEST);
        }
        if (priceList.getPriceForCDW() != null) {
            createAdvertisementDTO.setCDW(true);
        } else {
            createAdvertisementDTO.setCDW(false);
        }

        return advertisementRepository.save(createAdvertisementDTO);
    }

    @Override
    public Advertisement edit(Long id, Advertisement advertisement) {
        //TODO: CHECK IF CAR IS AVAILABLE !!!!
        //TODO: CHECK IF ADVERTISEMENT IS EDITABLE
        advertisement.setCar(carService.get(advertisement.getCar().getId()));
        advertisement.setPriceList(priceListService.get(advertisement.getPriceList().getId()));
        PriceList priceList = advertisement.getPriceList();
        if (priceList.getPricePerKm() != null && advertisement.getKilometresLimit() == null) {
            throw new InvalidAdvertisementDataException("You have to set kilometres limit.", HttpStatus.BAD_REQUEST);
        }
        if (priceList.getPriceForCDW() != null) {
            advertisement.setCDW(true);
        } else {
            advertisement.setCDW(false);
        }

        return advertisementRepository.save(advertisement);
    }

    @Override
    public void delete(Long id) {
        //TODO: CHECK
        Advertisement advertisement = get(id);
        advertisement.setLogicalStatus(LogicalStatus.DELETED);
        advertisementRepository.save(advertisement);
    }

    @Override
    public Advertisement get(Long id) {
        Advertisement advertisement = advertisementRepository.findOneById(id);
        if (advertisement == null) {
            throw new InvalidAdvertisementDataException("This advertisement doesn't exist.", HttpStatus.NOT_FOUND);
        }
        return advertisement;
    }

    @Autowired
    public AdvertisementServiceImpl(CarService carService, PriceListService priceListService, AdvertisementRepository advertisementRepository) {
        this.carService = carService;
        this.priceListService = priceListService;
        this.advertisementRepository = advertisementRepository;
    }
}
