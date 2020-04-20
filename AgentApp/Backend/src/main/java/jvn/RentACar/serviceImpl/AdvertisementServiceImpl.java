package jvn.RentACar.serviceImpl;

import jvn.RentACar.dto.both.AdvertisementDTO;
import jvn.RentACar.dto.response.AdvertisementWithPicturesDTO;
import jvn.RentACar.enumeration.LogicalStatus;
import jvn.RentACar.exceptionHandler.InvalidAdvertisementDataException;
import jvn.RentACar.mapper.AdvertisementDtoMapper;
import jvn.RentACar.model.Advertisement;
import jvn.RentACar.model.Picture;
import jvn.RentACar.model.PriceList;
import jvn.RentACar.repository.AdvertisementRepository;
import jvn.RentACar.service.AdvertisementService;
import jvn.RentACar.service.CarService;
import jvn.RentACar.service.PriceListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdvertisementServiceImpl implements AdvertisementService {

    private CarService carService;

    private PriceListService priceListService;

    private AdvertisementRepository advertisementRepository;

    private AdvertisementDtoMapper advertisementMapper;

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

    @Override
    public List<AdvertisementWithPicturesDTO> getAll() {
        List<Advertisement> ads = advertisementRepository.findAllByLogicalStatusNot(LogicalStatus.DELETED);
        List<AdvertisementWithPicturesDTO> adsDTOList = new ArrayList<>();
        for (Advertisement ad : ads) {
            AdvertisementDTO advertisementDTO = advertisementMapper.toDto(ad);
            List<String> pictures = new ArrayList<>();
            for (Picture picture : ad.getCar().getPictures()) {
                pictures.add(picture.getData());
            }
            adsDTOList.add(new AdvertisementWithPicturesDTO(advertisementDTO, pictures));
        }
        return adsDTOList;
    }

    @Autowired
    public AdvertisementServiceImpl(CarService carService, PriceListService priceListService,
                                    AdvertisementRepository advertisementRepository, AdvertisementDtoMapper advertisementMapper) {
        this.carService = carService;
        this.priceListService = priceListService;
        this.advertisementRepository = advertisementRepository;
        this.advertisementMapper = advertisementMapper;
    }
}
