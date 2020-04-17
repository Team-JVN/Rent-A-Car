package jvn.RentACar.serviceImpl;

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
        //PROVERI DA LI JE TADA AUTO SLOBODAN
        return advertisementRepository.save(createAdvertisementDTO);
    }

    @Autowired
    public AdvertisementServiceImpl(CarService carService, PriceListService priceListService, AdvertisementRepository advertisementRepository) {
        this.carService = carService;
        this.priceListService = priceListService;
        this.advertisementRepository = advertisementRepository;
    }
}
