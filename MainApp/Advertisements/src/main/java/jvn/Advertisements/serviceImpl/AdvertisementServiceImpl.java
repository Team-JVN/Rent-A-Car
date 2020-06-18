package jvn.Advertisements.serviceImpl;

import jvn.Advertisements.client.CarClient;
import jvn.Advertisements.client.RentingClient;
import jvn.Advertisements.dto.message.AdvertisementMessageDTO;
import jvn.Advertisements.dto.message.OwnerMessageDTO;
import jvn.Advertisements.dto.request.AdvertisementEditDTO;
import jvn.Advertisements.dto.request.UserDTO;
import jvn.Advertisements.dto.response.CarWithAllInformationDTO;
import jvn.Advertisements.enumeration.EditType;
import jvn.Advertisements.enumeration.LogicalStatus;
import jvn.Advertisements.exceptionHandler.InvalidAdvertisementDataException;
import jvn.Advertisements.mapper.AdvertisementMessageDtoMapper;
import jvn.Advertisements.model.Advertisement;
import jvn.Advertisements.model.PriceList;
import jvn.Advertisements.producer.AdvertisementProducer;
import jvn.Advertisements.repository.AdvertisementRepository;
import jvn.Advertisements.service.AdvertisementService;
import jvn.Advertisements.service.PriceListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdvertisementServiceImpl implements AdvertisementService {

    private PriceListService priceListService;

    private AdvertisementRepository advertisementRepository;

    private AdvertisementMessageDtoMapper advertisementMessageMapper;

    private AdvertisementProducer advertisementProducer;

    private CarClient carClient;

    private RentingClient rentingClient;

    @Override
    @Transactional
    public Advertisement create(Advertisement createAdvertisementDTO, UserDTO userDTO) {

        checkDate(createAdvertisementDTO.getDateFrom(), createAdvertisementDTO.getDateTo());
        CarWithAllInformationDTO carDTO = carClient.verify(userDTO.getId(), createAdvertisementDTO.getCar());
        checkIfCarIsAvailable(createAdvertisementDTO.getCar(), createAdvertisementDTO.getDateFrom(),
                createAdvertisementDTO.getDateTo());
        createAdvertisementDTO.setOwner(userDTO.getId());
        createAdvertisementDTO
                .setPriceList(priceListService.get(createAdvertisementDTO.getPriceList().getId(), userDTO.getId()));
        createAdvertisementDTO = setPriceList(createAdvertisementDTO, createAdvertisementDTO.getKilometresLimit());

        if (userDTO.getRole().equals("ROLE_CLIENT")) {
            createAdvertisementDTO.setDiscount(null);
            if (createAdvertisementDTO.getDateTo() == null) {
                throw new InvalidAdvertisementDataException("You have to set date to.", HttpStatus.BAD_REQUEST);
            }
            checkIfClientCanCreateAdvertisement(createAdvertisementDTO);
        } else {
            createAdvertisementDTO.setDateTo(null);
        }

        Advertisement savedAdvertisement = advertisementRepository.save(createAdvertisementDTO);
        sendMessageToSearchService(savedAdvertisement, userDTO, carDTO);
        return savedAdvertisement;
    }

    @Override
    public List<Advertisement> get(List<Long> advertisements) {
        return advertisementRepository.findByIdInAndLogicalStatus(advertisements, LogicalStatus.EXISTING);
    }

    @Override
    public Advertisement getOne(Long advertisementId) {
        return advertisementRepository.findById(advertisementId).orElse(null);
    }

    public void delete(Long id, Long loggedInUserId) {

        Advertisement advertisement = get(id, LogicalStatus.EXISTING);
        checkOwner(advertisement, loggedInUserId);

        if (rentingClient.canDeleteAdvertisement(id)) {
            advertisement.setLogicalStatus(LogicalStatus.DELETED);
            advertisementRepository.save(advertisement);
            sendMessageToSearchService(advertisement.getId());
            rejectAllRequests(id);
        } else {
            throw new InvalidAdvertisementDataException(
                    "This advertisement is in use and therefore it cannot be deleted.", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Advertisement edit(Long id, Advertisement advertisement, UserDTO userDTO) {
        Advertisement dbAdvertisement = get(id, LogicalStatus.EXISTING);
        checkOwner(dbAdvertisement, userDTO.getId());
        if (!rentingClient.getAdvertisementEditType(id).equals(EditType.ALL)) {
            throw new InvalidAdvertisementDataException(
                    "This advertisement is in use and therefore it cannot be edited.", HttpStatus.BAD_REQUEST);
        }

        if (!dbAdvertisement.getCar().equals(advertisement.getCar())) {
            dbAdvertisement.setCar(advertisement.getCar());
            checkIfCarIsAvailable(dbAdvertisement.getCar(), dbAdvertisement.getDateFrom(), dbAdvertisement.getDateTo());
        }
        CarWithAllInformationDTO carDTO = carClient.verify(userDTO.getId(), dbAdvertisement.getCar());
        dbAdvertisement.setDateFrom(advertisement.getDateFrom());
        dbAdvertisement.setDateTo(advertisement.getDateTo());
        dbAdvertisement.setPriceList(priceListService.get(advertisement.getPriceList().getId(), userDTO.getId()));
        dbAdvertisement.setPickUpPoint(advertisement.getPickUpPoint());
        dbAdvertisement = setPriceList(dbAdvertisement, advertisement.getKilometresLimit());
        dbAdvertisement.setDiscount(advertisement.getDiscount());
        dbAdvertisement = advertisementRepository.save(dbAdvertisement);

        sendMessageToSearchService(dbAdvertisement, userDTO, carDTO);
        return dbAdvertisement;
    }

    @Override
    public Advertisement editPartial(Long id, AdvertisementEditDTO advertisement, Long loggedInUserId) {
        Advertisement dbAdvertisement = get(id, LogicalStatus.EXISTING);
        checkOwner(dbAdvertisement, loggedInUserId);
        dbAdvertisement.setPriceList(priceListService.get(advertisement.getPriceList().getId(), loggedInUserId));
        dbAdvertisement = setPriceList(dbAdvertisement, advertisement.getKilometresLimit());
        dbAdvertisement.setDiscount(advertisement.getDiscount());
        advertisement.setId(id);
        editAdvertisement(advertisement);
        return advertisementRepository.save(dbAdvertisement);
    }

    @Override
    public EditType getCarEditType(Long carId) {
        List<Advertisement> advertisements = advertisementRepository.findByCar(carId);
        if (advertisements != null && !advertisements.isEmpty()) {
            return EditType.PARTIAL;
        }
        return EditType.ALL;
    }

    @Override
    public Boolean canEditCarPartially(Long carId) {
        List<Advertisement> advertisements = advertisementRepository.findByCarAndLogicalStatus(carId, LogicalStatus.EXISTING);
        if (advertisements == null || advertisements.isEmpty()) {
            return true;
        } else {
            return !rentingClient.hasRentInfos(advertisements.stream().map(Advertisement::getId).collect(Collectors.toList()));
        }
    }

    @Override
    public Boolean canDeleteCar(Long carId) {
        List<Advertisement> advertisements = advertisementRepository
                .findByCarAndLogicalStatusAndDateToGreaterThanEqualOrCarAndLogicalStatusAndDateToEquals(carId,
                        LogicalStatus.EXISTING, LocalDate.now(), carId, LogicalStatus.EXISTING, null);
        return advertisements == null || advertisements.isEmpty();
    }

    @Override
    public boolean checkIfCanDeleteAndDelete(Long id, Long loggedInUser) {
        Advertisement advertisement = get(id, LogicalStatus.EXISTING);
        checkOwner(advertisement, loggedInUser);

        if (rentingClient.canDeleteAdvertisement(id)) {
            advertisement.setLogicalStatus(LogicalStatus.DELETED);
            advertisementRepository.save(advertisement);
            sendMessageToSearchService(advertisement.getId());
            rejectAllRequests(id);
            return true;
        }
        return false;
    }

    @Override
    public String getAdvertisementEditType(Long id, Long loggedInUser) {
        if (rentingClient.getAdvertisementEditType(id).equals(EditType.ALL)) {
            return "ALL";
        }
        return "PARTIAL";
    }

    @Override
    public List<Advertisement> getAll(Long loggedInUser) {
        return advertisementRepository.findAllByOwner(loggedInUser);
    }

    @Override
    public boolean checkIfCarIsAvailableForSoap(Long carId, LocalDate dateFrom, LocalDate dateTo) {
        if (!advertisementRepository.findByCarAndLogicalStatusNotAndDateToEquals(carId, LogicalStatus.DELETED, null)
                .isEmpty()) {
            return false;
        }

        if (dateTo == null) {
            if (!advertisementRepository.findByCarAndLogicalStatusNotAndDateToGreaterThanEqual(carId,
                    LogicalStatus.DELETED, dateFrom).isEmpty()) {
                return false;
            }
        } else {
            if (!advertisementRepository.findByCarAndLogicalStatusNotAndDateFromLessThanEqualAndDateToGreaterThanEqual(
                    carId, LogicalStatus.DELETED, dateTo, dateFrom).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Async
    public void editAdvertisement(AdvertisementEditDTO advertisementEditDTO) {
        advertisementProducer.sendMessageForSearch(advertisementEditDTO);
    }

    @Async
    public void rejectAllRequests(Long advId) {
        advertisementProducer.sendMessageToRentingService(advId);
    }

    @Async
    public void sendMessageToSearchService(Advertisement savedAdvertisement, UserDTO userDTO,
                                           CarWithAllInformationDTO carDTO) {
        AdvertisementMessageDTO advertisementMessageDTO = advertisementMessageMapper.toDto(savedAdvertisement);
        advertisementMessageDTO.setCar(carDTO);
        advertisementMessageDTO.setOwner(new OwnerMessageDTO(userDTO.getId(), userDTO.getName(), userDTO.getEmail()));
        advertisementProducer.sendMessageForSearch(advertisementMessageDTO);
    }

    @Async
    public void sendMessageToSearchService(Long advId) {
        advertisementProducer.sendMessageForSearch(advId);
    }

    private void checkOwner(Advertisement advertisement, Long loggedInUserId) {
        if (!advertisement.getOwner().equals(loggedInUserId)) {
            throw new InvalidAdvertisementDataException(
                    "You are not the owner of this advertisement, therefore you cannot edit or delete it.",
                    HttpStatus.BAD_REQUEST);
        }
    }

    private void checkIfCarIsAvailable(Long carId, LocalDate advertisementDateFrom, LocalDate advertisementDateTo) {
        if (!advertisementRepository.findByCarAndLogicalStatusNotAndDateToEquals(carId, LogicalStatus.DELETED, null)
                .isEmpty()) {
            throw new InvalidAdvertisementDataException("Active advertisement for this car already exist!",
                    HttpStatus.BAD_REQUEST);
        }

        if (advertisementDateTo == null) {
            if (!advertisementRepository.findByCarAndLogicalStatusNotAndDateToGreaterThanEqual(carId,
                    LogicalStatus.DELETED, advertisementDateFrom).isEmpty()) {
                throw new InvalidAdvertisementDataException("Active advertisement for this car already exist!",
                        HttpStatus.BAD_REQUEST);
            }
        } else {
            if (!advertisementRepository.findByCarAndLogicalStatusNotAndDateFromLessThanEqualAndDateToGreaterThanEqual(
                    carId, LogicalStatus.DELETED, advertisementDateTo, advertisementDateFrom).isEmpty()) {
                throw new InvalidAdvertisementDataException("Active advertisement for this car already exist!",
                        HttpStatus.BAD_REQUEST);
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

    private void checkIfClientCanCreateAdvertisement(Advertisement advertisement) {
        if (advertisementRepository.findByOwnerAndDateToGreaterThanEqualAndLogicalStatus(advertisement.getOwner(),
                LocalDate.now(), LogicalStatus.EXISTING).size() >= 3) {
            throw new InvalidAdvertisementDataException(
                    "You already have 3 active advertisements, therefore you cannot create a new one.",
                    HttpStatus.BAD_REQUEST);
        }
    }

    private Advertisement get(Long id, LogicalStatus status) {
        Advertisement advertisement = advertisementRepository.findByIdAndLogicalStatus(id, status);
        if (advertisement == null) {
            throw new InvalidAdvertisementDataException("Requested advertisement does not exist.",
                    HttpStatus.NOT_FOUND);
        }
        return advertisement;
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

    @Autowired
    public AdvertisementServiceImpl(PriceListService priceListService, CarClient carClient,
                                    AdvertisementRepository advertisementRepository, AdvertisementMessageDtoMapper advertisementMessageMapper,
                                    AdvertisementProducer advertisementProducer, RentingClient rentingClient) {
        this.priceListService = priceListService;
        this.carClient = carClient;
        this.advertisementRepository = advertisementRepository;
        this.advertisementMessageMapper = advertisementMessageMapper;
        this.advertisementProducer = advertisementProducer;
        this.rentingClient = rentingClient;
    }
}
