package jvn.RentACar.serviceImpl;

import jvn.RentACar.dto.request.AdvertisementEditDTO;
import jvn.RentACar.enumeration.EditType;
import jvn.RentACar.enumeration.LogicalStatus;
import jvn.RentACar.enumeration.RentRequestStatus;
import jvn.RentACar.exceptionHandler.InvalidAdvertisementDataException;
import jvn.RentACar.exceptionHandler.InvalidCarDataException;
import jvn.RentACar.mapper.AdvertisementDtoMapper;
import jvn.RentACar.model.Advertisement;
import jvn.RentACar.model.Car;
import jvn.RentACar.model.PriceList;
import jvn.RentACar.repository.AdvertisementRepository;
import jvn.RentACar.service.*;
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

    private UserService userService;

    @Override
    public Advertisement create(Advertisement createAdvertisementDTO) {

        checkDate(createAdvertisementDTO.getDateFrom(), createAdvertisementDTO.getDateTo());
        createAdvertisementDTO.setCar(carService.get(createAdvertisementDTO.getCar().getId()));
        checkOwner(createAdvertisementDTO.getCar());

        checkIfCarIsAvailable(createAdvertisementDTO.getCar().getId(), createAdvertisementDTO.getDateFrom(), createAdvertisementDTO.getDateTo());
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
        checkIfCarIsAvailableForEdit(advertisement.getCar().getId(), advertisement.getDateFrom(), advertisement.getDateTo(), advertisement.getId());
        dbAdvertisement.setDateFrom(advertisement.getDateFrom());
        dbAdvertisement.setCar(carService.get(advertisement.getCar().getId()));
        checkOwner(dbAdvertisement.getCar());
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
    public Advertisement editPartial(Long id, AdvertisementEditDTO advertisement) {
        Advertisement dbAdvertisement = get(id);
        checkOwner(dbAdvertisement.getCar());
        dbAdvertisement.setPriceList(priceListService.get(advertisement.getPriceList().getId()));
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
    public EditType getEditType(Long id) {
        Advertisement advertisement = get(id);
        if (advertisementRepository.findByIdAndRentInfosRentRequestRentRequestStatusNotAndLogicalStatus(advertisement.getId(), RentRequestStatus.CANCELED, LogicalStatus.EXISTING) != null) {
            return EditType.PARTIAL;
        }
        return EditType.ALL;
    }

    @Override
    public void delete(Long id) {
        Advertisement advertisement = get(id);
        //TODO: Odbi sve zahteve koji su PENDING
        checkOwner(advertisement.getCar());
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

    private void checkIfCarIsAvailable(Long carId, LocalDate advertisementDateFrom, LocalDate advertisementDateTo) {
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

    private void checkIfCarIsAvailableForEdit(Long carId, LocalDate advertisementDateFrom, LocalDate advertisementDateTo, Long advertisementId) {
        List<Advertisement> advertisements = advertisementRepository.findByCarIdAndLogicalStatusNotAndDateToEquals(carId, LogicalStatus.DELETED, null);
        if (!advertisements.isEmpty() && advertisements.size() != 1) {
            throw new InvalidAdvertisementDataException("Active advertisement for this car already exist!", HttpStatus.FORBIDDEN);
        }
        if (advertisements.size() == 1 && !advertisements.get(0).getId().equals(advertisementId)) {
            throw new InvalidAdvertisementDataException("Active advertisement for this car already exist!", HttpStatus.FORBIDDEN);
        }
        if (advertisementDateTo == null) {
            advertisements = advertisementRepository.findByCarIdAndLogicalStatusNotAndDateToGreaterThanEqual(carId, LogicalStatus.DELETED, advertisementDateFrom);
            if (!advertisements.isEmpty()) {
                if (advertisements.size() == 1 && !advertisements.get(0).getId().equals(advertisementId)) {
                    throw new InvalidAdvertisementDataException("Active advertisement for this car already exist!", HttpStatus.FORBIDDEN);
                }
            }
        } else {
            advertisements = advertisementRepository.findByCarIdAndLogicalStatusNotAndDateFromLessThanEqualAndDateToGreaterThanEqual(carId, LogicalStatus.DELETED, advertisementDateTo, advertisementDateFrom);
            if (!advertisements.isEmpty()) {
                if (advertisements.size() == 1 && !advertisements.get(0).getId().equals(advertisementId)) {
                    throw new InvalidAdvertisementDataException("Active advertisement for this car already exist!", HttpStatus.FORBIDDEN);
                }
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

    private void checkOwner(Car car) {
        if (!userService.getLoginAgent().getEmail().equals(car.getOwner().getEmail())) {
            throw new InvalidCarDataException("You are not owner of this car.", HttpStatus.FORBIDDEN);
        }
    }

    @Autowired
    public AdvertisementServiceImpl(CarService carService, PriceListService priceListService, RentInfoService rentInfoService, UserService userService,
                                    AdvertisementRepository advertisementRepository, AdvertisementDtoMapper advertisementMapper) {
        this.carService = carService;
        this.priceListService = priceListService;
        this.advertisementRepository = advertisementRepository;
        this.advertisementMapper = advertisementMapper;
        this.userService = userService;
        this.rentInfoService = rentInfoService;
    }
}
