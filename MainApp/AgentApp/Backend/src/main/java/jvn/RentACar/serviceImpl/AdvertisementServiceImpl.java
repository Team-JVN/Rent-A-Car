package jvn.RentACar.serviceImpl;

import jvn.RentACar.client.AdvertisementClient;
import jvn.RentACar.dto.request.AdvertisementEditDTO;
import jvn.RentACar.dto.response.SearchParamsDTO;
import jvn.RentACar.dto.soap.advertisement.*;
import jvn.RentACar.enumeration.EditType;
import jvn.RentACar.enumeration.LogicalStatus;
import jvn.RentACar.enumeration.RentRequestStatus;
import jvn.RentACar.exceptionHandler.InvalidAdvertisementDataException;
import jvn.RentACar.exceptionHandler.InvalidCarDataException;
import jvn.RentACar.mapper.AdvertisementDetailsMapper;
import jvn.RentACar.model.Advertisement;
import jvn.RentACar.model.Car;
import jvn.RentACar.model.Log;
import jvn.RentACar.model.PriceList;
import jvn.RentACar.repository.AdvertisementRepository;
import jvn.RentACar.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdvertisementServiceImpl implements AdvertisementService {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private CarService carService;

    private PriceListService priceListService;

    private AdvertisementRepository advertisementRepository;

    private UserService userService;

    private AdvertisementClient advertisementClient;

    private AdvertisementDetailsMapper advertisementDetailsMapper;

    private LogService logService;

    @Override
    public Advertisement create(Advertisement createAdvertisementDTO) {
        checkDate(createAdvertisementDTO.getDateFrom(), createAdvertisementDTO.getDateTo());
        createAdvertisementDTO.setCar(carService.get(createAdvertisementDTO.getCar().getId()));
        checkOwner(createAdvertisementDTO.getCar());
        checkIfCarIsAvailable(createAdvertisementDTO.getCar().getId(), createAdvertisementDTO.getDateFrom(), createAdvertisementDTO.getDateTo());
        createAdvertisementDTO.setPriceList(priceListService.get(createAdvertisementDTO.getPriceList().getId()));
        createAdvertisementDTO = setPriceList(createAdvertisementDTO, createAdvertisementDTO.getKilometresLimit());
        createAdvertisementDTO.setMainAppId(saveInMainApp(createAdvertisementDTO));
        return advertisementRepository.save(createAdvertisementDTO);
    }

    @Override
    public Advertisement edit(Long id, Advertisement advertisement) {
        Advertisement dbAdvertisement = get(advertisement.getId());
        isEditable(dbAdvertisement);
        if (!getEditType(dbAdvertisement.getId()).equals(EditType.ALL)) {
            throw new InvalidAdvertisementDataException(
                    "This advertisement is in use and therefore it cannot be edited.", HttpStatus.BAD_REQUEST);
        }
        if (!dbAdvertisement.getCar().getId().equals(advertisement.getCar().getId())) {
            dbAdvertisement.setCar(advertisement.getCar());
            checkIfCarIsAvailable(dbAdvertisement.getCar().getId(), dbAdvertisement.getDateFrom(), dbAdvertisement.getDateTo());
        }
        dbAdvertisement.setDateFrom(advertisement.getDateFrom());
        dbAdvertisement.setCar(carService.get(advertisement.getCar().getId()));
        checkOwner(dbAdvertisement.getCar());
        dbAdvertisement.setPriceList(priceListService.get(advertisement.getPriceList().getId()));
        dbAdvertisement.setPickUpPoint(advertisement.getPickUpPoint());
        dbAdvertisement = setPriceList(dbAdvertisement, advertisement.getKilometresLimit());
        dbAdvertisement.setDiscount(advertisement.getDiscount());
        saveInMainApp(dbAdvertisement);
        return advertisementRepository.save(dbAdvertisement);
    }

    @Override
    public Advertisement editPartial(Long id, AdvertisementEditDTO advertisement) {
        Advertisement dbAdvertisement = get(id);
        checkOwner(dbAdvertisement.getCar());
        dbAdvertisement.setPriceList(priceListService.get(advertisement.getPriceList().getId()));
        dbAdvertisement = setPriceList(dbAdvertisement, advertisement.getKilometresLimit());
        dbAdvertisement.setDiscount(advertisement.getDiscount());
        advertisementClient.editPartial(dbAdvertisement);
        return advertisementRepository.save(dbAdvertisement);
    }

    @Override
    public EditType getEditType(Long id) {
        Advertisement advertisement = get(id);
        GetAdvertisementEditTypeResponse response = advertisementClient.getEditType(advertisement);
        if (response != null) {
            if (response.getEditType().equals("ALL")) {
                return EditType.ALL;
            }
        }
        return EditType.PARTIAL;
    }

    @Override
    public void delete(Long id) {
        Advertisement advertisement = get(id);
        checkOwner(advertisement.getCar());
        DeleteAdvertisementDetailsResponse response = advertisementClient.checkAndDeleteIfCan(advertisement);
        if (response == null || !response.isCanDelete()) {
            throw new InvalidAdvertisementDataException("This advertisement is in use and therefore can not be deleted.", HttpStatus.BAD_REQUEST);
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

    @Override
    public List<Advertisement> searchAdvertisements(SearchParamsDTO searchParamsDTO) {

        LocalDateTime dateTimeFrom = getDateConverted(searchParamsDTO.getDateTimeFrom());
        LocalDateTime dateTimeTo = getDateConverted(searchParamsDTO.getDateTimeTo());
        LocalDateTime todayPlus48h = (LocalDateTime.now().minusMinutes(3)).plusDays(2);
        if (dateTimeFrom.isBefore(todayPlus48h)) {
            throw new InvalidAdvertisementDataException("Date and time from must be at least 48h from now.", HttpStatus.BAD_REQUEST);
        }
        if (dateTimeTo.isBefore(todayPlus48h)) {
            throw new InvalidAdvertisementDataException("Date and time to must be at least 48h from now.", HttpStatus.BAD_REQUEST);
        }
        synchronizeAdvertisements();
        SearchParamsDTO newSearchParamsDTO = replaceNullValues(searchParamsDTO);

        List<Advertisement> searchedAdsList;
        if (newSearchParamsDTO.getCDW() != null && newSearchParamsDTO.getCDW()) {
            searchedAdsList = advertisementRepository.findByCDWAndLogicalStatusAndDateFromLessThanEqualAndDateToIsNotNullAndDateToGreaterThanEqualAndPickUpPointContainsIgnoringCaseAndCarMakeNameContainsAndCarModelNameContainsAndCarFuelTypeNameContainsAndCarGearBoxTypeNameContainsAndCarBodyStyleNameContainsAndCarMileageInKmLessThanEqualAndCarKidsSeatsGreaterThanEqualAndCarAvgRatingGreaterThanEqualAndPriceListPricePerDayBetweenOrCDWAndLogicalStatusAndDateFromLessThanEqualAndDateToIsNullAndPickUpPointContainsIgnoringCaseAndCarMakeNameContainsAndCarModelNameContainsAndCarFuelTypeNameContainsAndCarGearBoxTypeNameContainsAndCarBodyStyleContainsAndCarMileageInKmLessThanEqualAndCarKidsSeatsGreaterThanEqualAndCarAvgRatingGreaterThanEqualAndPriceListPricePerDayBetween(
                    newSearchParamsDTO.getCDW(), LogicalStatus.EXISTING, dateTimeFrom.toLocalDate(), dateTimeTo.toLocalDate(), newSearchParamsDTO.getPickUpPoint(),
                    newSearchParamsDTO.getMake(), newSearchParamsDTO.getModel(), newSearchParamsDTO.getFuelType(), newSearchParamsDTO.getGearBoxType(),
                    newSearchParamsDTO.getBodyStyle(), newSearchParamsDTO.getMileageInKm(), newSearchParamsDTO.getKidsSeats(), newSearchParamsDTO.getMinRating().doubleValue(),
                    newSearchParamsDTO.getMinPricePerDay(), newSearchParamsDTO.getMaxPricePerDay(),
                    newSearchParamsDTO.getCDW(), LogicalStatus.EXISTING, dateTimeFrom.toLocalDate(), newSearchParamsDTO.getPickUpPoint(), newSearchParamsDTO.getMake(),
                    newSearchParamsDTO.getModel(), newSearchParamsDTO.getFuelType(), newSearchParamsDTO.getGearBoxType(), newSearchParamsDTO.getBodyStyle(),
                    newSearchParamsDTO.getMileageInKm(), newSearchParamsDTO.getKidsSeats(), newSearchParamsDTO.getMinRating().doubleValue(),
                    newSearchParamsDTO.getMinPricePerDay(), newSearchParamsDTO.getMaxPricePerDay()
            );
        } else {
            searchedAdsList = advertisementRepository.findByLogicalStatusAndDateFromLessThanEqualAndDateToIsNotNullAndDateToGreaterThanEqualAndPickUpPointContainsIgnoringCaseAndCarMakeNameContainsAndCarModelNameContainsAndCarFuelTypeNameContainsAndCarGearBoxTypeNameContainsAndCarBodyStyleNameContainsAndCarMileageInKmLessThanEqualAndCarKidsSeatsGreaterThanEqualAndCarAvgRatingGreaterThanEqualAndPriceListPricePerDayBetweenOrLogicalStatusAndDateFromLessThanEqualAndDateToIsNullAndPickUpPointContainsIgnoringCaseAndCarMakeNameContainsAndCarModelNameContainsAndCarFuelTypeNameContainsAndCarGearBoxTypeNameContainsAndCarBodyStyleNameContainsAndCarMileageInKmLessThanEqualAndCarKidsSeatsGreaterThanEqualAndCarAvgRatingGreaterThanEqualAndPriceListPricePerDayBetween(
                    LogicalStatus.EXISTING, dateTimeFrom.toLocalDate(), dateTimeTo.toLocalDate(), newSearchParamsDTO.getPickUpPoint(),
                    newSearchParamsDTO.getMake(), newSearchParamsDTO.getModel(), newSearchParamsDTO.getFuelType(), newSearchParamsDTO.getGearBoxType(),
                    newSearchParamsDTO.getBodyStyle(), newSearchParamsDTO.getMileageInKm(), newSearchParamsDTO.getKidsSeats(), newSearchParamsDTO.getMinRating().doubleValue(),
                    newSearchParamsDTO.getMinPricePerDay(), newSearchParamsDTO.getMaxPricePerDay(),
                    LogicalStatus.EXISTING, dateTimeFrom.toLocalDate(), newSearchParamsDTO.getPickUpPoint(), newSearchParamsDTO.getMake(),
                    newSearchParamsDTO.getModel(), newSearchParamsDTO.getFuelType(), newSearchParamsDTO.getGearBoxType(), newSearchParamsDTO.getBodyStyle(),
                    newSearchParamsDTO.getMileageInKm(), newSearchParamsDTO.getKidsSeats(), newSearchParamsDTO.getMinRating().doubleValue(),
                    newSearchParamsDTO.getMinPricePerDay(), newSearchParamsDTO.getMaxPricePerDay()
            );
        }

        searchedAdsList.stream().filter(ad -> ad.getKilometresLimit() == null || ad.getKilometresLimit() >= newSearchParamsDTO.getKilometresLimit()).collect(Collectors.toList());

        return searchedAdsList;
    }

    @Override
    public List<Advertisement> getAll(String status) {
        synchronizeAdvertisements();
        List<Advertisement> ads;
        switch (status) {
            case "all":
                ads = advertisementRepository.findAllByLogicalStatusNot(LogicalStatus.DELETED);
                break;
            case "active":
                ads = advertisementRepository.findAllByLogicalStatusNotAndDateToEqualsOrLogicalStatusNotAndDateToGreaterThan(LogicalStatus.DELETED, null, LogicalStatus.DELETED,
                        LocalDate.now());
                break;
            default:
                ads = advertisementRepository.findAllByLogicalStatusNotAndDateToLessThanEqual(LogicalStatus.DELETED, LocalDate.now());
                break;
        }
        return ads;
    }

    private LocalDateTime getDateConverted(String dateTime) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(dateTime, formatter);
    }

    private SearchParamsDTO replaceNullValues(SearchParamsDTO searchParamsDTO) {
        if (searchParamsDTO.getMake() == null) {
            searchParamsDTO.setMake("");
        }
        if (searchParamsDTO.getModel() == null) {
            searchParamsDTO.setModel("");
        }
        if (searchParamsDTO.getMinRating() == null) {
            searchParamsDTO.setMinRating(0);
        }
        if (searchParamsDTO.getFuelType() == null) {
            searchParamsDTO.setFuelType("");
        }
        if (searchParamsDTO.getGearBoxType() == null) {
            searchParamsDTO.setGearBoxType("");
        }
        if (searchParamsDTO.getBodyStyle() == null) {
            searchParamsDTO.setBodyStyle("");
        }
        if (searchParamsDTO.getMinPricePerDay() == null) {
            searchParamsDTO.setMinPricePerDay(0.0);
        }
        if (searchParamsDTO.getMaxPricePerDay() == null) {
            searchParamsDTO.setMaxPricePerDay(Double.MAX_VALUE);
        }
        if (searchParamsDTO.getKidsSeats() == null) {
            searchParamsDTO.setKidsSeats(0);
        }
        if (searchParamsDTO.getMileageInKm() == null) {
            searchParamsDTO.setMileageInKm(Integer.MAX_VALUE);
        }
        if (searchParamsDTO.getKilometresLimit() == null) {
            searchParamsDTO.setKilometresLimit(0);
        }

        return searchParamsDTO;
    }

    private void checkIfCarIsAvailable(Long carId, LocalDate advertisementDateFrom, LocalDate advertisementDateTo) {
        if (!advertisementRepository.findByCarIdAndLogicalStatusNotAndDateToEquals(carId, LogicalStatus.DELETED, null).isEmpty()) {
            throw new InvalidAdvertisementDataException("Active advertisement for this car already exist!", HttpStatus.BAD_REQUEST);
        }

        if (advertisementDateTo == null) {
            if (!advertisementRepository.findByCarIdAndLogicalStatusNotAndDateToGreaterThanEqual(carId, LogicalStatus.DELETED, advertisementDateFrom).isEmpty()) {
                throw new InvalidAdvertisementDataException("Active advertisement for this car already exist!", HttpStatus.BAD_REQUEST);
            }
        } else {
            if (!advertisementRepository.findByCarIdAndLogicalStatusNotAndDateFromLessThanEqualAndDateToGreaterThanEqual(carId, LogicalStatus.DELETED, advertisementDateTo, advertisementDateFrom).isEmpty()) {
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

    private void isEditable(Advertisement advertisement) {
        if (advertisementRepository.findByIdAndRentInfosRentRequestRentRequestStatusNotAndLogicalStatus(advertisement.getId(), RentRequestStatus.CANCELED, LogicalStatus.EXISTING) != null) {
            throw new InvalidAdvertisementDataException("This advertisement is in use and therefore can not be edited/deleted.", HttpStatus.BAD_REQUEST);
        }
    }

    private void checkOwner(Car car) {
        if (!userService.getLoginAgent().getEmail().equals(car.getOwner().getEmail())) {
            logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CHO", String.format("User %s is not the owner of advertisement %s", userService.getLoginUser().getId(), car.getId())));
            throw new InvalidCarDataException("You are not owner of this car.", HttpStatus.BAD_REQUEST);
        }
    }

    private Advertisement setPriceList(Advertisement dbAdvertisement, Integer kilometresLimit) {
        PriceList priceList = dbAdvertisement.getPriceList();
        if (priceList.getPricePerKm() != null && kilometresLimit == null) {
            throw new InvalidAdvertisementDataException("You have to set kilometres limit.", HttpStatus.BAD_REQUEST);
        }
        if (priceList.getPricePerKm() == null) {
            dbAdvertisement.setKilometresLimit(null);
        } else {
            dbAdvertisement.setKilometresLimit(kilometresLimit);
        }
        if (priceList.getPriceForCDW() != null) {
            dbAdvertisement.setCDW(true);
        } else {
            dbAdvertisement.setCDW(false);
        }
        return dbAdvertisement;
    }

    private Long saveInMainApp(Advertisement advertisement) {
        try {
            CreateOrEditAdvertisementDetailsResponse response = advertisementClient.createOrEdit(advertisement);
            return response.getAdvertisementDetails().getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void synchronizeAdvertisements() {
        try {
            GetAllAdvertisementDetailsResponse response = advertisementClient.getAll();
            if (response == null) {
                return;
            }
            List<AdvertisementDetails> advertisementDetails = response.getAdvertisementDetails();
            if (advertisementDetails == null || advertisementDetails.isEmpty()) {
                return;
            }

            for (AdvertisementDetails current : advertisementDetails) {
                Advertisement advertisement = advertisementDetailsMapper.toEntity(current);
                Advertisement dbAdvertisement = advertisementRepository.findByMainAppId(advertisement.getMainAppId());
                if (dbAdvertisement == null) {
                    createSynchronize(advertisement);
                } else {
                    editSynchronize(advertisement, dbAdvertisement);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createSynchronize(Advertisement advertisement) {
        if (advertisement.getCar() == null || advertisement.getPriceList() == null) {
            return;
        }
        Advertisement dbAdvertisement = advertisementRepository.saveAndFlush(advertisement);
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CAD", String.format("[SOAP Sync] Advertisement %s successfully created", dbAdvertisement.getId())));
    }

    private void editSynchronize(Advertisement advertisement, Advertisement dbAdvertisement) {
        if (advertisement.getCar() == null || advertisement.getPriceList() == null) {
            return;
        }
        dbAdvertisement.setCar(advertisement.getCar());
        dbAdvertisement.setPriceList(advertisement.getPriceList());
        dbAdvertisement.setKilometresLimit(advertisement.getKilometresLimit());
        dbAdvertisement.setDiscount(advertisement.getDiscount());
        dbAdvertisement.setDateFrom(advertisement.getDateFrom());
        dbAdvertisement.setDateTo(advertisement.getDateTo());
        dbAdvertisement.setPickUpPoint(advertisement.getPickUpPoint());
        dbAdvertisement.setCDW(advertisement.getCDW());
        dbAdvertisement.setLogicalStatus(advertisement.getLogicalStatus());
        advertisementRepository.save(dbAdvertisement);
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "EAD", String.format("[SOAP Sync] Advertisement %s successfully edited", dbAdvertisement.getId())));
    }

    @Autowired
    public AdvertisementServiceImpl(CarService carService, PriceListService priceListService, UserService userService,
                                    AdvertisementRepository advertisementRepository, AdvertisementClient advertisementClient,
                                    AdvertisementDetailsMapper advertisementDetailsMapper, LogService logService) {
        this.carService = carService;
        this.priceListService = priceListService;
        this.advertisementRepository = advertisementRepository;
        this.userService = userService;
        this.advertisementClient = advertisementClient;
        this.advertisementDetailsMapper = advertisementDetailsMapper;
        this.logService = logService;
    }
}
