package jvn.RentACar.serviceImpl;

import jvn.RentACar.enumeration.LogicalStatus;
import jvn.RentACar.enumeration.RentRequestStatus;
import jvn.RentACar.exceptionHandler.InvalidAdvertisementDataException;
import jvn.RentACar.mapper.AdvertisementDtoMapper;
import jvn.RentACar.model.Advertisement;
import jvn.RentACar.model.PriceList;
import jvn.RentACar.repository.AdvertisementRepository;
import jvn.RentACar.service.AdvertisementService;
import jvn.RentACar.service.CarService;
import jvn.RentACar.service.PriceListService;
import jvn.RentACar.service.RentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdvertisementServiceImpl implements AdvertisementService {

    private CarService carService;

    private PriceListService priceListService;

    private AdvertisementRepository advertisementRepository;

    private AdvertisementDtoMapper advertisementMapper;

    private RentInfoService rentInfoService;

    @Override
    public Advertisement create(Advertisement createAdvertisementDTO) {
        //TODO: Ako klijent kreira oglas onda dateTo ne sme biti null!
        checkDate(createAdvertisementDTO.getDateFrom(), createAdvertisementDTO.getDateTo());
        checkIfCarIsAvailable(createAdvertisementDTO.getCar().getId(), createAdvertisementDTO.getDateFrom(), createAdvertisementDTO.getDateTo(), null);

        createAdvertisementDTO.setCar(carService.get(createAdvertisementDTO.getCar().getId()));
        createAdvertisementDTO.setPriceList(priceListService.get(createAdvertisementDTO.getPriceList().getId()));
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
        return advertisementRepository.save(createAdvertisementDTO);
    }

    @Override
    public Advertisement edit(Long id, Advertisement advertisement) {
        Advertisement dbAdvertisement = get(advertisement.getId());
        isEditable(dbAdvertisement);
        checkIfCarIsAvailable(advertisement.getCar().getId(), advertisement.getDateFrom(), advertisement.getDateTo(), advertisement.getId());
        dbAdvertisement.setDateFrom(advertisement.getDateFrom());
        dbAdvertisement.setCar(carService.get(advertisement.getCar().getId()));
        dbAdvertisement.setPriceList(priceListService.get(advertisement.getPriceList().getId()));
        dbAdvertisement.setPickUpPoint(advertisement.getPickUpPoint());
        PriceList priceList = dbAdvertisement.getPriceList();
        if (priceList.getPricePerKm() != null && advertisement.getKilometresLimit() == null) {
            throw new InvalidAdvertisementDataException("You have to set kilometres limit.", HttpStatus.BAD_REQUEST);
        }
        if (priceList.getPricePerKm() == null) {
            dbAdvertisement.setKilometresLimit(null);
        } else {
            dbAdvertisement.setKilometresLimit(advertisement.getKilometresLimit());
        }
        if (priceList.getPriceForCDW() != null) {
            dbAdvertisement.setCDW(true);
        } else {
            dbAdvertisement.setCDW(false);
        }

        dbAdvertisement.setDiscount(advertisement.getDiscount());
        return advertisementRepository.save(dbAdvertisement);
    }

    @Override
    public void delete(Long id) {
        Advertisement advertisement = get(id);
        //TODO: Odbi sve zahteve koji su PENDING
        if (advertisementRepository.findByIdAndLogicalStatusAndRentInfosRentRequestRentRequestStatusAndRentInfosDateTimeToGreaterThanEqual(advertisement.getId(), LogicalStatus.EXISTING, RentRequestStatus.PAID, LocalDateTime.now()) != null) {
            throw new InvalidAdvertisementDataException("This advertisement is in use and therefore can not be deleted.", HttpStatus.FORBIDDEN);
        }
        advertisement.setLogicalStatus(LogicalStatus.DELETED);
        advertisementRepository.save(advertisement);
    }

    @Override
    public Advertisement get(Long id) {
        Advertisement advertisement = advertisementRepository.findByIdAndLogicalStatus(id, LogicalStatus.EXISTING);
        if (advertisement == null) {
            throw new InvalidAdvertisementDataException("Requested advertisement does not exist.", HttpStatus.NOT_FOUND);
        }
        return advertisement;
    }

    public List<Advertisement> getAll(String status) {
        List<Advertisement> ads = null;
        if (status.equals("all")) {
            ads = advertisementRepository.findAllByLogicalStatusNot(LogicalStatus.DELETED);
        } else if (status.equals("active")) {
            ads = advertisementRepository.findAllByLogicalStatusNotAndDateToEqualsOrLogicalStatusNotAndDateToGreaterThan(LogicalStatus.DELETED, null, LogicalStatus.DELETED,
                    LocalDate.now());
        } else {
            ads = advertisementRepository.findAllByLogicalStatusNotAndDateToLessThanEqual(LogicalStatus.DELETED, LocalDate.now());
        }
        return ads;
    }

    private void checkIfCarIsAvailable(Long carId, LocalDate advertisementDateFrom, LocalDate advertisementDateTo, Long advertisementId) {
        if (!advertisementRepository.findByCarIdAndLogicalStatusNotAndDateToEquals(carId, LogicalStatus.DELETED, null).isEmpty()) {
            throw new InvalidAdvertisementDataException("Active advertisement for this car already exist!", HttpStatus.FORBIDDEN);
        }

        if (advertisementDateTo == null) {
            if (!advertisementRepository.findByCarIdAndLogicalStatusNotAndDateToGreaterThanEqual(carId, LogicalStatus.DELETED, advertisementDateFrom).isEmpty()) {
                throw new InvalidAdvertisementDataException("Active advertisement for this car already exist!", HttpStatus.FORBIDDEN);
            }
        } else {
            if (!advertisementRepository.findByCarIdAndLogicalStatusNotAndDateFromLessThanEqualAndDateToGreaterThanEqual(carId, LogicalStatus.DELETED, advertisementDateTo, advertisementDateFrom).isEmpty()) {
                throw new InvalidAdvertisementDataException("Active advertisement for this car already exist!", HttpStatus.FORBIDDEN);
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

    private void isEditable(Advertisement advertisement) {
        if (advertisementRepository.findByIdAndRentInfosRentRequestRentRequestStatusNotAndLogicalStatus(advertisement.getId(), RentRequestStatus.CANCELED, LogicalStatus.EXISTING) != null) {
            throw new InvalidAdvertisementDataException("This advertisement is in use and therefore can not be edited/deleted.", HttpStatus.BAD_REQUEST);
        }
    }

    @Autowired
    public AdvertisementServiceImpl(CarService carService, PriceListService priceListService, RentInfoService rentInfoService,
                                    AdvertisementRepository advertisementRepository, AdvertisementDtoMapper advertisementMapper) {
        this.carService = carService;
        this.priceListService = priceListService;
        this.advertisementRepository = advertisementRepository;
        this.advertisementMapper = advertisementMapper;
        this.rentInfoService = rentInfoService;
    }
}
