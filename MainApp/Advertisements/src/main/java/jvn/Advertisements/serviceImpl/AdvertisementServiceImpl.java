package jvn.Advertisements.serviceImpl;

import jvn.Advertisements.client.CarClient;
import jvn.Advertisements.dto.message.AdvertisementMessageDTO;
import jvn.Advertisements.dto.response.CarWithAllInformationDTO;
import jvn.Advertisements.dto.request.UserDTO;
import jvn.Advertisements.enumeration.LogicalStatus;
import jvn.Advertisements.exceptionHandler.InvalidAdvertisementDataException;
import jvn.Advertisements.mapper.AdvertisementDtoMapper;
import jvn.Advertisements.mapper.AdvertisementMessageDtoMapper;
import jvn.Advertisements.model.Advertisement;
import jvn.Advertisements.model.PriceList;
import jvn.Advertisements.producer.AdvertisementProducer;
import jvn.Advertisements.repository.AdvertisementRepository;
import jvn.Advertisements.service.AdvertisementService;
import jvn.Advertisements.service.PriceListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AdvertisementServiceImpl implements AdvertisementService {

    private PriceListService priceListService;

    private AdvertisementRepository advertisementRepository;

    private AdvertisementDtoMapper advertisementMapper;

    private AdvertisementMessageDtoMapper advertisementMessageMapper;

    private AdvertisementProducer advertisementProducer;

    private CarClient carClient;


    @Override
    public Advertisement create(Advertisement createAdvertisementDTO, UserDTO userDTO) {

        checkDate(createAdvertisementDTO.getDateFrom(), createAdvertisementDTO.getDateTo());
        CarWithAllInformationDTO carDTO = carClient.verify(createAdvertisementDTO.getCar());
        //TODO: Treba  car servisu da dodas metodu koja ce proveriti da li car postoji i da li je ulogovani korisnik vlasnik tog car-a
        //        checkOwner(createAdvertisementDTO.getCar());
        checkIfCarIsAvailable(createAdvertisementDTO.getCar(), createAdvertisementDTO.getDateFrom(), createAdvertisementDTO.getDateTo());
        createAdvertisementDTO.setPriceList(priceListService.get(createAdvertisementDTO.getPriceList().getId(),userDTO));
        PriceList priceList = createAdvertisementDTO.getPriceList();
        if (priceList.getPricePerKm() != null && createAdvertisementDTO.getKilometresLimit() == null) {
            throw new InvalidAdvertisementDataException("You have to set kilometres limit.", HttpStatus.BAD_REQUEST);
        }
        if (priceList.getPricePerKm() == null) {
            createAdvertisementDTO.setKilometresLimit(null);
        }
        if (priceList.getPriceForCDW() != null) {
            createAdvertisementDTO.setCDW(true);
        } else {
            createAdvertisementDTO.setCDW(false);
        }

        Advertisement savedAdvertisement = advertisementRepository.save(createAdvertisementDTO);
        AdvertisementMessageDTO advertisementMessageDTO = advertisementMessageMapper.toDto(savedAdvertisement);
        advertisementMessageDTO.setCar(carDTO);
        advertisementProducer.send(advertisementMessageDTO);
        return savedAdvertisement;
    }

    public List<Advertisement> getAll(String status) {
//        List<Advertisement> ads = null;
//        if (status.equals("all")) {
//            ads = advertisementRepository.findAllByLogicalStatusNot(LogicalStatus.DELETED);
//        } else if (status.equals("active")) {
//            ads = advertisementRepository.findAllByLogicalStatusNotAndDateToEqualsOrLogicalStatusNotAndDateToGreaterThan(LogicalStatus.DELETED, null, LogicalStatus.DELETED,
//                    LocalDate.now());
//        } else {
//            ads = advertisementRepository.findAllByLogicalStatusNotAndDateToLessThanEqual(LogicalStatus.DELETED, LocalDate.now());
//        }
//        return ads;
        return null;
    }

    private void checkIfCarIsAvailable(Long carId, LocalDate advertisementDateFrom, LocalDate advertisementDateTo) {
        if (!advertisementRepository.findByCarAndLogicalStatusNotAndDateToEquals(carId, LogicalStatus.DELETED, null).isEmpty()) {
            throw new InvalidAdvertisementDataException("Active advertisement for this car already exist!", HttpStatus.BAD_REQUEST);
        }

        if (advertisementDateTo == null) {
            if (!advertisementRepository.findByCarAndLogicalStatusNotAndDateToGreaterThanEqual(carId, LogicalStatus.DELETED, advertisementDateFrom).isEmpty()) {
                throw new InvalidAdvertisementDataException("Active advertisement for this car already exist!", HttpStatus.BAD_REQUEST);
            }
        } else {
            if (!advertisementRepository.findByCarAndLogicalStatusNotAndDateFromLessThanEqualAndDateToGreaterThanEqual(carId, LogicalStatus.DELETED, advertisementDateTo, advertisementDateFrom).isEmpty()) {
                throw new InvalidAdvertisementDataException("Active advertisement for this car already exist!", HttpStatus.BAD_REQUEST);
            }
        }
    }

    private void checkDate(LocalDate dateFrom, LocalDate dateTo) {
        if (dateFrom.isBefore(LocalDate.now()) || (dateTo != null && dateTo.isBefore(LocalDate.now()))) {
            throw new InvalidAdvertisementDataException("Invalid date from/to.", HttpStatus.BAD_REQUEST);
        }
        if (dateTo != null && dateTo.isBefore(dateFrom)) {
            throw new InvalidAdvertisementDataException("Date To cannot be before Date From.", HttpStatus.BAD_REQUEST);
        }
    }

    @Autowired
    public AdvertisementServiceImpl(PriceListService priceListService, CarClient carClient, AdvertisementRepository advertisementRepository,
                                    AdvertisementDtoMapper advertisementMapper, AdvertisementMessageDtoMapper advertisementMessageMapper,
                                    AdvertisementProducer advertisementProducer) {
        this.priceListService = priceListService;
        this.carClient = carClient;
        this.advertisementRepository = advertisementRepository;
        this.advertisementMapper = advertisementMapper;
        this.advertisementMessageMapper = advertisementMessageMapper;
        this.advertisementProducer = advertisementProducer;
    }
}
