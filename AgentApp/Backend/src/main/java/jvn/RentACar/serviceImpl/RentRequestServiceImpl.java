package jvn.RentACar.serviceImpl;

import jvn.RentACar.dto.request.RentRequestStatusDTO;
import jvn.RentACar.enumeration.RentRequestStatus;
import jvn.RentACar.exceptionHandler.InvalidRentRequestDataException;
import jvn.RentACar.model.*;
import jvn.RentACar.repository.RentRequestRepository;
import jvn.RentACar.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RentRequestServiceImpl implements RentRequestService {

    private ClientService clientService;

    private AdvertisementService advertisementService;

    private RentRequestRepository rentRequestRepository;

    private RentInfoService rentInfoService;

    private UserService userService;

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public RentRequest create(RentRequest rentRequest) {
        User user = userService.getLoginUser();
        if (user instanceof Agent) {
            // TODO: You need to refuse all other requests and send email to client
            rentRequest.setRentRequestStatus(RentRequestStatus.PAID);
            if (rentRequest.getClient() == null) {
                throw new InvalidRentRequestDataException("Please choose a client.", HttpStatus.FORBIDDEN);
            }
            rentRequest.setClient(clientService.get(rentRequest.getClient().getId()));
        } else if (user instanceof Client) {
            rentRequest.setRentRequestStatus(RentRequestStatus.PENDING);
            rentRequest.setClient((Client) user);
        }

        rentRequest.setCreatedBy(user);
        rentRequest.setTotalPrice(0.0);
        Set<RentInfo> rentInfos = rentRequest.getRentInfos();
        rentRequest.setRentInfos(new HashSet<>());
        RentRequest savedRequest = rentRequestRepository.save(rentRequest);
        rentRequest.setTotalPrice(setRentInfosData(savedRequest, rentInfos));
        savedRequest.setRentInfos(rentInfos);
        return rentRequestRepository.save(savedRequest);
    }

    @Override
    public List<RentRequest> getMine(String status) {
        User loggedInUser = userService.getLoginUser();
        if (status.equals("all")) {
            return rentRequestRepository.findByClientEmail(loggedInUser.getEmail());
        }
        return rentRequestRepository.findByClientEmailAndRentRequestStatus(loggedInUser.getEmail(), getRentRequestStatus(status));
    }

    @Override
    public List<RentRequest> get(Long advertisementId, String status) {
        Advertisement advertisement = advertisementService.get(advertisementId);
        if (!userService.getLoginAgent().getEmail().equals(advertisement.getCar().getOwner().getEmail())) {
            throw new InvalidRentRequestDataException("This rent request is not yours.", HttpStatus.FORBIDDEN);
        }
        if (status.equals("all")) {
            return rentRequestRepository.findByRentInfosAdvertisementId(advertisementId);
        }
        return rentRequestRepository.findByRentInfosAdvertisementIdAndRentRequestStatus(advertisementId, getRentRequestStatus(status));
    }

    @Override
    public RentRequest get(Long id) {
        RentRequest rentRequest = rentRequestRepository.findOneById(id);
        if (rentRequest == null) {
            throw new InvalidRentRequestDataException("Requested rent request does not exist.", HttpStatus.NOT_FOUND);
        }
        return rentRequest;
    }

    @Override
    public void delete(Long id) {
        RentRequest rentRequest = get(id);
        checkCreator(rentRequest);
        if (rentRequest.getRentRequestStatus().equals(RentRequestStatus.PAID)) {
            throw new InvalidRentRequestDataException("This rent request is PAID so you can not delete it.",
                    HttpStatus.FORBIDDEN);
        }
        rentRequest.setClient(null);
        Set<RentInfo> rentInfos = rentRequest.getRentInfos();
        rentRequest.setRentInfos(new HashSet<>());
        rentRequest.setCreatedBy(null);
        rentInfoService.delete(rentInfos);
        rentRequestRepository.deleteById(id);
    }

    @Override
    public RentRequest changeRentRequestStatus(Long id, RentRequestStatusDTO newStatus) {
        RentRequestStatus rentRequestStatus = getRentRequestStatus(newStatus.getStatus());
        RentRequest rentRequest = get(id);

        if (rentRequestStatus.equals(RentRequestStatus.CANCELED)) {
            if (userService.getLoginUser().getEmail().equals(rentRequest.getCreatedBy().getEmail())) {
                return cancel(rentRequest);
            } else {
                return reject(rentRequest);
            }
        } else if (rentRequestStatus.equals(RentRequestStatus.PAID)) {
            return accept(rentRequest);
        }
        throw new InvalidRentRequestDataException("This rent request's status doesn't exist.", HttpStatus.FORBIDDEN);
    }

    private RentRequest cancel(RentRequest rentRequest) {
        if (!userService.getLoginUser().getEmail().equals(rentRequest.getCreatedBy().getEmail())) {
            throw new InvalidRentRequestDataException("This rent request is not yours so you can't cancel it.", HttpStatus.FORBIDDEN);
        }
        if (!rentRequest.getRentRequestStatus().equals(RentRequestStatus.PENDING)) {
            throw new InvalidRentRequestDataException("Your request is already expected, therefore you can not cancel it.", HttpStatus.FORBIDDEN);
        }
        rentRequest.setRentRequestStatus(RentRequestStatus.CANCELED);
        return rentRequestRepository.save(rentRequest);
    }

    private RentRequest reject(RentRequest rentRequest) {
        RentInfo rentInfo = (RentInfo) rentRequest.getRentInfos().toArray()[0];
        User advertisementsOwner = rentInfo.getAdvertisement().getCar().getOwner();
        if (!userService.getLoginUser().getEmail().equals(advertisementsOwner.getEmail())) {
            throw new InvalidRentRequestDataException("You aren't owner of rent request's advertisement so you can't reject this request.", HttpStatus.FORBIDDEN);
        }

        if (!rentRequest.getRentRequestStatus().equals(RentRequestStatus.PENDING)) {
            throw new InvalidRentRequestDataException("This request is already expected, therefore you can not reject it.", HttpStatus.FORBIDDEN);
        }

        rentRequest.setRentRequestStatus(RentRequestStatus.CANCELED);
        //TODO: Posalji mejl klijentu koji je kreirao taj zahtev
        return rentRequestRepository.save(rentRequest);
    }

    private RentRequest accept(RentRequest rentRequest) {
        RentInfo rentInfo = (RentInfo) rentRequest.getRentInfos().toArray()[0];
        User advertisementsOwner = rentInfo.getAdvertisement().getCar().getOwner();
        if (!userService.getLoginUser().getEmail().equals(advertisementsOwner.getEmail())) {
            throw new InvalidRentRequestDataException("You aren't owner of rent request's advertisement so you can't accept this request.", HttpStatus.FORBIDDEN);
        }
        //TODO:Implement this method.
//        if (!rentRequest.getRentRequestStatus().equals(RentRequestStatus.PENDING)) {
//            throw new InvalidRentRequestDataException("This request is already expected, therefore you can not reject it.", HttpStatus.FORBIDDEN);
//        }
//
//        rentRequest.setRentRequestStatus(RentRequestStatus.CANCELED);
        return rentRequest;
    }

    private void checkCreator(RentRequest rentRequest) {
        if (!userService.getLoginUser().getEmail().equals(rentRequest.getCreatedBy().getEmail())) {
            throw new InvalidRentRequestDataException("This rent request is not yours so you can't delete it.", HttpStatus.FORBIDDEN);
        }
    }

    private double setRentInfosData(RentRequest rentRequest, Set<RentInfo> rentInfos) {
        double totalPrice = 0;
        for (RentInfo rentInfo : rentInfos) {
            rentInfo.setRentRequest(rentRequest);
            Advertisement advertisement = advertisementService.get(rentInfo.getAdvertisement().getId());
            checkDate(advertisement, rentInfo.getDateTimeFrom().toLocalDate(), rentInfo.getDateTimeTo().toLocalDate(), rentInfo.getDateTimeFrom(), rentInfo.getDateTimeTo());
            rentInfo.setAdvertisement(advertisement);
            if (!advertisement.getCDW()) {
                rentInfo.setOptedForCDW(null);
            }
            rentInfo.setRating(0);
            rentInfo.setKilometresLimit(advertisement.getKilometresLimit());
            rentInfo.setPricePerKm(advertisement.getPriceList().getPricePerKm());
            totalPrice += countPrice(rentInfo);
        }

        User owner = ((RentInfo) rentInfos.toArray()[0]).getAdvertisement().getCar().getOwner();
        for (RentInfo rentInfo : rentInfos) {
            if (!rentInfo.getAdvertisement().getCar().getOwner().getEmail().equals(owner.getEmail())) {
                throw new InvalidRentRequestDataException("All advertisements of a rent request must have the same owner.", HttpStatus.FORBIDDEN);
            }
        }
        return totalPrice;
    }


    private void checkDate(Advertisement advertisement, LocalDate rentInfoDateFrom, LocalDate rentInfoDateTo, LocalDateTime rentInfoDateTimeFrom, LocalDateTime rentInfoDateTimeTo) {
        LocalDate advertisementDateFrom = advertisement.getDateFrom();
        if (rentInfoDateFrom.isBefore(LocalDate.now()) || rentInfoDateTo.isBefore(LocalDate.now())) {
            throw new InvalidRentRequestDataException("Invalid date from/to.",
                    HttpStatus.NOT_FOUND);
        }
        if (rentInfoDateTo.isBefore(rentInfoDateFrom)) {
            throw new InvalidRentRequestDataException("Date To cannot be before Date From.",
                    HttpStatus.NOT_FOUND);
        }
        if (rentInfoDateFrom.isBefore(advertisementDateFrom) || rentInfoDateTo.isBefore(advertisementDateFrom)) {
            throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.",
                    HttpStatus.FORBIDDEN);
        }
        LocalDate advertisementDateTo = advertisement.getDateTo();
        if (advertisementDateTo != null) {
            if (rentInfoDateFrom.isAfter(advertisementDateTo) || rentInfoDateTo.isAfter(advertisementDateTo)) {
                throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.",
                        HttpStatus.FORBIDDEN);
            }
        }
        rentInfoDateTimeFrom = LocalDateTime.of(rentInfoDateFrom.minusDays(1), LocalTime.of(23, 59));
        rentInfoDateTimeTo = LocalDateTime.of(rentInfoDateTo.plusDays(1), LocalTime.of(0, 0));
        if (!rentRequestRepository.findByRentRequestStatusAndRentInfosDateTimeFromLessThanEqualAndRentInfosDateTimeToGreaterThanEqual(RentRequestStatus.PAID, rentInfoDateTimeFrom, rentInfoDateTimeFrom).isEmpty()) {
            throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.",
                    HttpStatus.FORBIDDEN);
        }
        if (!rentRequestRepository.findByRentRequestStatusAndRentInfosDateTimeFromLessThanEqualAndRentInfosDateTimeToGreaterThanEqual(RentRequestStatus.PAID, rentInfoDateTimeTo, rentInfoDateTimeTo).isEmpty()) {
            throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.",
                    HttpStatus.FORBIDDEN);
        }
        if (!rentRequestRepository.findByRentRequestStatusAndRentInfosDateTimeFromGreaterThanEqualAndRentInfosDateTimeToLessThanEqual(RentRequestStatus.PAID, rentInfoDateTimeFrom, rentInfoDateTimeTo).isEmpty()) {
            throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.",
                    HttpStatus.FORBIDDEN);
        }

    }

    private double countPrice(RentInfo rentInfo) {
        double price = 0;
        PriceList priceList = rentInfo.getAdvertisement().getPriceList();
        long numberOfHours = ChronoUnit.HOURS.between(rentInfo.getDateTimeFrom(), rentInfo.getDateTimeTo());
        double numberOfDays = Math.ceil(numberOfHours / 24.0);
        if (rentInfo.getOptedForCDW() != null && rentInfo.getOptedForCDW()) {
            price += (numberOfDays * priceList.getPriceForCDW());
        }
        if (numberOfDays > 30 && rentInfo.getAdvertisement().getDiscount() != null) {
            double newPricePerDay = priceList.getPricePerDay()
                    - (priceList.getPricePerDay() * (rentInfo.getAdvertisement().getDiscount() / 100.0));
            price += (numberOfDays * newPricePerDay);
        } else {
            price += (numberOfDays * priceList.getPricePerDay());
        }
        return price;
    }

    private RentRequestStatus getRentRequestStatus(String status) {
        try {
            return RentRequestStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new InvalidRentRequestDataException("Please choose some of existing rent request's status.",
                    HttpStatus.NOT_FOUND);
        }
    }

    @Autowired
    public RentRequestServiceImpl(ClientService clientService, AdvertisementService advertisementService, UserService userService,
                                  RentRequestRepository rentRequestRepository, RentInfoService rentInfoService) {
        this.clientService = clientService;
        this.advertisementService = advertisementService;
        this.rentRequestRepository = rentRequestRepository;
        this.rentInfoService = rentInfoService;
        this.userService = userService;
    }
}
