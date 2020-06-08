package jvn.RentACar.serviceImpl;

import jvn.RentACar.dto.request.RentRequestStatusDTO;
import jvn.RentACar.enumeration.RentRequestStatus;
import jvn.RentACar.exceptionHandler.InvalidRentRequestDataException;
import jvn.RentACar.model.*;
import jvn.RentACar.repository.RentRequestRepository;
import jvn.RentACar.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
public class RentRequestServiceImpl implements RentRequestService {

    private ClientService clientService;

    private AdvertisementService advertisementService;

    private RentRequestRepository rentRequestRepository;

    private UserService userService;

    private EmailNotificationService emailNotificationService;

    private Environment environment;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public RentRequest create(RentRequest rentRequest) {
        User user = userService.getLoginUser();
        Advertisement dbAvd = advertisementService.get((new ArrayList<>(rentRequest.getRentInfos())).get(0).getAdvertisement().getId());
        Long ownerId = dbAvd.getCar().getOwner().getId();
        if (ownerId.equals(user.getId())) {
            if (rentRequest.getClient() == null || rentRequest.getClient().equals(user.getId())) {
                throw new InvalidRentRequestDataException("Please choose client for which you create rent request.", HttpStatus.BAD_REQUEST);
            }
            rentRequest.setClient(clientService.get(rentRequest.getClient().getId()));
            rentRequest.setRentRequestStatus(RentRequestStatus.PAID);
        } else {
            rentRequest.setClient((Client) user);
            rentRequest.setRentRequestStatus(RentRequestStatus.PENDING);
        }
        rentRequest.setCreatedBy(user);
        rentRequest.setTotalPrice(0.0);
        Set<RentInfo> rentInfos =  rentRequest.getRentInfos();
        rentRequest.setRentInfos(new HashSet<>());
        RentRequest savedRequest = rentRequestRepository.save(rentRequest);
        savedRequest = rentRequestRepository.save(setRentInfosData(savedRequest,rentInfos));
        if (savedRequest.getRentRequestStatus().equals(RentRequestStatus.PAID)) {
            rejectOtherRequests(savedRequest);
        } else if (savedRequest.getRentRequestStatus().equals(RentRequestStatus.PENDING)) {
            Instant today = (new Date()).toInstant();
            executeRejectTask(savedRequest.getId(), today.plus(24, ChronoUnit.HOURS));
        }

        return savedRequest;
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
            throw new InvalidRentRequestDataException("This rent request is not yours.", HttpStatus.BAD_REQUEST);
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
    public RentRequest changeRentRequestStatus(Long id, RentRequestStatusDTO newStatus) {
        RentRequestStatus rentRequestStatus = getRentRequestStatus(newStatus.getStatus());
        RentRequest rentRequest = get(id);
        if (!rentRequest.getRentRequestStatus().equals(RentRequestStatus.PENDING)) {
            throw new InvalidRentRequestDataException("Your request is already expected, therefore you can not cancel/accept/reject it.", HttpStatus.BAD_REQUEST);
        }
        if (rentRequestStatus.equals(RentRequestStatus.CANCELED)) {
            if (userService.getLoginUser().getId().equals(rentRequest.getCreatedBy().getId())) {
                return cancel(rentRequest);
            } else {
                return reject(rentRequest);
            }
        } else if (rentRequestStatus.equals(RentRequestStatus.PAID)) {
            return accept(rentRequest);
        }
        throw new InvalidRentRequestDataException("This rent request's status doesn't exist.", HttpStatus.BAD_REQUEST);
    }

    @Override
    public void rejectAllRequests(Long advId) {
        List<RentRequest> rentRequests = rentRequestRepository.findByRentInfosAdvertisementIdAndRentInfosRentRequestRentRequestStatus(advId, RentRequestStatus.PENDING);
        for (RentRequest rentRequest : rentRequests) {
            rentRequest.setRentRequestStatus(RentRequestStatus.CANCELED);
            composeAndSendRejectedRentRequest(rentRequest.getClient().getEmail(), rentRequest.getId());
        }
        rentRequestRepository.saveAll(rentRequests);
    }

    private RentRequest cancel(RentRequest rentRequest) {
        rentRequest.setRentRequestStatus(RentRequestStatus.CANCELED);
        return rentRequestRepository.save(rentRequest);
    }

    private RentRequest reject(RentRequest rentRequest) {
        RentInfo rentInfo = (RentInfo) rentRequest.getRentInfos().toArray()[0];
        User advertisementsOwner = rentInfo.getAdvertisement().getCar().getOwner();
        if (!userService.getLoginUser().getId().equals(advertisementsOwner.getId())) {
            throw new InvalidRentRequestDataException("You aren't owner of rent request's advertisement so you can't reject this request.", HttpStatus.BAD_REQUEST);
        }
        rentRequest.setRentRequestStatus(RentRequestStatus.CANCELED);
        composeAndSendRejectedRentRequest(rentRequest.getClient().getEmail(),rentRequest.getId());
        return rentRequestRepository.save(rentRequest);
    }

    private RentRequest accept(RentRequest rentRequest) {
        RentInfo rentInfo = (RentInfo) rentRequest.getRentInfos().toArray()[0];
        User advertisementsOwner = rentInfo.getAdvertisement().getCar().getOwner();
        if (!userService.getLoginUser().getId().equals(advertisementsOwner.getId())) {
            throw new InvalidRentRequestDataException("You aren't owner of rent request's advertisement so you can't accept this request.", HttpStatus.BAD_REQUEST);
        }
        checkIfCanAcceptRentRequest(rentRequest);
        rentRequest.setRentRequestStatus(RentRequestStatus.PAID);
        RentRequest paidRentRequest = rentRequestRepository.save(rentRequest);
        rejectOtherRequests(rentRequest);
        composeAndSendAcceptedRentRequest(rentRequest.getClient().getEmail(), rentRequest.getId());
        return paidRentRequest;
    }


    private void checkIfCanAcceptRentRequest(RentRequest rentRequest) {
        for (RentInfo rentInfo : rentRequest.getRentInfos()) {
            LocalDateTime rentInfoDateTimeFrom = LocalDateTime.of(rentInfo.getDateTimeFrom().toLocalDate().minusDays(1), LocalTime.of(23, 59));
            LocalDateTime rentInfoDateTimeTo = LocalDateTime.of(rentInfo.getDateTimeTo().toLocalDate().plusDays(1), LocalTime.of(0, 0));
            if (!rentRequestRepository.findByRentRequestStatusAndRentInfosDateTimeFromLessThanEqualAndRentInfosDateTimeToGreaterThanAndRentInfosAdvertisementId(RentRequestStatus.PAID, rentInfoDateTimeFrom, rentInfoDateTimeFrom, rentInfo.getAdvertisement().getId()).isEmpty()) {
                throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.",
                        HttpStatus.BAD_REQUEST);
            }
            if (!rentRequestRepository.findByRentRequestStatusAndRentInfosDateTimeFromLessThanEqualAndRentInfosDateTimeToGreaterThanAndRentInfosAdvertisementId(RentRequestStatus.PAID, rentInfoDateTimeTo, rentInfoDateTimeTo, rentInfo.getAdvertisement().getId()).isEmpty()) {
                throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.",
                        HttpStatus.BAD_REQUEST);
            }
            if (!rentRequestRepository.findByRentRequestStatusAndRentInfosDateTimeFromGreaterThanEqualAndRentInfosDateTimeToLessThanAndRentInfosAdvertisementId(RentRequestStatus.PAID, rentInfoDateTimeFrom, rentInfoDateTimeTo, rentInfo.getAdvertisement().getId()).isEmpty()) {
                throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.",
                        HttpStatus.BAD_REQUEST);
            }
        }
    }
    private RentRequest setRentInfosData(RentRequest rentRequest, Set<RentInfo> rentInfos) {
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
        rentRequest.setTotalPrice(totalPrice);
        rentRequest.setRentInfos(new HashSet<>(rentInfos));
        getAdvertisementOwnerId(rentRequest.getRentInfos());
        return rentRequest;
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
                    HttpStatus.BAD_REQUEST);
        }
        LocalDate advertisementDateTo = advertisement.getDateTo();
        if (advertisementDateTo != null) {
            if (rentInfoDateFrom.isAfter(advertisementDateTo) || rentInfoDateTo.isAfter(advertisementDateTo)) {
                throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.",
                        HttpStatus.BAD_REQUEST);
            }
        }

        rentInfoDateTimeFrom = LocalDateTime.of(rentInfoDateFrom.minusDays(1), LocalTime.of(23, 59));
        rentInfoDateTimeTo = LocalDateTime.of(rentInfoDateTo.plusDays(1), LocalTime.of(0, 0));
        if (!rentRequestRepository.findByRentRequestStatusAndRentInfosDateTimeFromLessThanEqualAndRentInfosDateTimeToGreaterThanAndRentInfosAdvertisementId(RentRequestStatus.PAID, rentInfoDateTimeFrom, rentInfoDateTimeFrom, advertisement.getId()).isEmpty()) {
            throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.",
                    HttpStatus.BAD_REQUEST);
        }
        if (!rentRequestRepository.findByRentRequestStatusAndRentInfosDateTimeFromLessThanEqualAndRentInfosDateTimeToGreaterThanAndRentInfosAdvertisementId(RentRequestStatus.PAID, rentInfoDateTimeTo, rentInfoDateTimeTo, advertisement.getId()).isEmpty()) {
            throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.",
                    HttpStatus.BAD_REQUEST);
        }
        if (!rentRequestRepository.findByRentRequestStatusAndRentInfosDateTimeFromGreaterThanEqualAndRentInfosDateTimeToLessThanAndRentInfosAdvertisementId(RentRequestStatus.PAID, rentInfoDateTimeFrom, rentInfoDateTimeTo, advertisement.getId()).isEmpty()) {
            throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.",
                    HttpStatus.BAD_REQUEST);
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

    private void composeAndSendRejectedRentRequest(String recipientEmail, Long rentRequestId) {
        String subject = "Rejected rent request";
        StringBuilder sb = new StringBuilder();
        sb.append("Your rent request is rejected by advertisement's owner");
        sb.append(System.lineSeparator());
        sb.append("To see rejected rent request click the following link:");
        sb.append(System.lineSeparator());
        sb.append(getLocalhostURL());
        sb.append("client-rent-request/");
        sb.append(rentRequestId);
        sb.append(System.lineSeparator());
        String text = sb.toString();
        emailNotificationService.sendEmail(recipientEmail, subject, text);
    }

    private void composeAndSendAcceptedRentRequest(String recipientEmail, Long rentRequestId) {
        String subject = "Accepted rent request";
        StringBuilder sb = new StringBuilder();
        sb.append("Your rent request is accepted by advertisement's owner");
        sb.append(System.lineSeparator());
        sb.append("To see accepted rent request click the following link:");
        sb.append(System.lineSeparator());
        sb.append(getLocalhostURL());
        sb.append("client-rent-request/");
        sb.append(rentRequestId);
        sb.append(System.lineSeparator());
        String text = sb.toString();
        emailNotificationService.sendEmail(recipientEmail, subject, text);
    }
    private Long getAdvertisementOwnerId(Set<RentInfo> rentInfos) {
        Set<Long> advertisements = new HashSet<>();
        Long ownerId = (new ArrayList<>(rentInfos)).get(0).getAdvertisement().getCar().getOwner().getId();
        for (RentInfo rentInfo : rentInfos) {
            Long advId = rentInfo.getAdvertisement().getId();
            if (!rentInfo.getAdvertisement().getCar().getOwner().getId().equals(ownerId)) {
                throw new InvalidRentRequestDataException("All advertisements of a rent request must have the same owner.", HttpStatus.BAD_REQUEST);
            }
            if (advertisements.contains(advId)) {
                throw new InvalidRentRequestDataException("You cannot choose the same car a couple of time. ", HttpStatus.BAD_REQUEST);
            }
            advertisements.add(rentInfo.getAdvertisement().getId());
        }
        return ownerId;
    }

    @Async
    public void rejectOtherRequests(RentRequest rentRequest) {
        for (RentInfo rentInfo : rentRequest.getRentInfos()) {
            LocalDateTime rentInfoDateTimeFrom = rentInfo.getDateTimeFrom();
            LocalDateTime rentInfoDateTimeTo = rentInfo.getDateTimeTo();
            Long advId = rentInfo.getAdvertisement().getId();
            List<RentRequest> rentRequests = rentRequestRepository.findByRentRequestStatusNotAndRentInfosDateTimeFromLessThanEqualAndRentInfosDateTimeToGreaterThanEqualAndRentInfosAdvertisementIdOrRentRequestStatusNotAndRentInfosDateTimeFromLessThanEqualAndRentInfosDateTimeToGreaterThanEqualAndRentInfosAdvertisementIdOrRentRequestStatusNotAndRentInfosDateTimeFromGreaterThanEqualAndRentInfosDateTimeToLessThanEqualAndRentInfosAdvertisementId(
                    RentRequestStatus.PAID, rentInfoDateTimeFrom, rentInfoDateTimeFrom, advId, RentRequestStatus.PAID, rentInfoDateTimeTo, rentInfoDateTimeTo, advId,
                    RentRequestStatus.PAID, rentInfoDateTimeFrom, rentInfoDateTimeTo, advId);
            for (RentRequest rentRequest1 : rentRequests) {
                rentRequest1.setRentRequestStatus(RentRequestStatus.CANCELED);
                composeAndSendRejectedRentRequest(rentRequest.getClient().getEmail(), rentRequest.getId());
            }
            if (!rentRequests.isEmpty()) {
                rentRequestRepository.saveAll(rentRequests);
            }
        }
    }

    @Async
    public void executeRejectTask(Long rentReqId, Instant executionMoment) {
        ScheduledExecutorService localExecutor = Executors.newSingleThreadScheduledExecutor();
        TaskScheduler scheduler = new ConcurrentTaskScheduler(localExecutor);
        scheduler.schedule(createRunnable(rentReqId), executionMoment);
    }

    private Runnable createRunnable(final Long rentReqId) {
        return () -> {
            RentRequest rentRequest = rentRequestRepository.findOneById(rentReqId);
            if (rentRequest.getRentRequestStatus() == RentRequestStatus.PENDING) {
                rentRequest.setRentRequestStatus(RentRequestStatus.CANCELED);
                rentRequestRepository.save(rentRequest);
                composeAndSendRejectedRentRequest(rentRequest.getClient().getEmail(), rentRequest.getId());
            }
        };
    }

    private String getLocalhostURL() {
        return environment.getProperty("LOCALHOST_URL");
    }

    @Autowired
    public RentRequestServiceImpl(ClientService clientService, AdvertisementService advertisementService, UserService userService,
                                  RentRequestRepository rentRequestRepository,
                                  EmailNotificationService emailNotificationService, Environment environment) {
        this.clientService = clientService;
        this.advertisementService = advertisementService;
        this.rentRequestRepository = rentRequestRepository;
        this.userService = userService;
        this.emailNotificationService = emailNotificationService;
        this.environment = environment;
    }
}
