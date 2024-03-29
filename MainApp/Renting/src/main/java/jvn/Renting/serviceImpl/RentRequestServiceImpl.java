package jvn.Renting.serviceImpl;

import com.netflix.ribbon.proxy.annotation.Http;
import jvn.Renting.client.AdvertisementClient;
import jvn.Renting.client.SearchClient;
import jvn.Renting.client.UserClient;
import jvn.Renting.dto.both.*;
import jvn.Renting.enumeration.CommentStatus;
import jvn.Renting.dto.message.Log;
import jvn.Renting.dto.request.RentRequestStatusDTO;
import jvn.Renting.enumeration.EditType;
import jvn.Renting.enumeration.RentRequestStatus;
import jvn.Renting.exceptionHandler.InvalidCommentDataException;
import jvn.Renting.exceptionHandler.InvalidMessageDataException;
import jvn.Renting.exceptionHandler.InvalidRentRequestDataException;
import jvn.Renting.mapper.CommentDtoMapper;
import jvn.Renting.mapper.MessageDtoMapper;
import jvn.Renting.mapper.RentReportDtoMapper;
import jvn.Renting.model.Comment;
import jvn.Renting.model.Message;
import jvn.Renting.model.RentInfo;
import jvn.Renting.model.RentRequest;
import jvn.Renting.repository.CommentRepository;
import jvn.Renting.producer.LogProducer;
import jvn.Renting.producer.RentRequestProducer;
import jvn.Renting.repository.RentInfoRepository;
import jvn.Renting.repository.RentRequestRepository;
import jvn.Renting.service.RentRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.SocketException;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

@Service
public class RentRequestServiceImpl implements RentRequestService {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private RentRequestRepository rentRequestRepository;

    private AdvertisementClient advertisementClient;

    private UserClient userClient;

    private SearchClient searchClient;

    private CommentDtoMapper commentDtoMapper;

    private CommentRepository commentRepository;

    private RentInfoRepository rentInfoRepository;

    private MessageDtoMapper messageDtoMapper;

    private RentReportDtoMapper rentReportDtoMapper;

    private RentRequestProducer rentRequestProducer;

    private LogProducer logProducer;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public RentRequest create(RentRequest rentRequest, Long loggedInUserId, Boolean canCreateRentRequests) throws ParseException {
        List<RentInfo> rentInfos = new ArrayList<>(rentRequest.getRentInfos());
        List<AdvertisementWithIdsDTO> advertisementDTOS = getAdvertisements(rentInfos);
        Long ownerId = getAdvertisementOwnerId(advertisementDTOS);
        rentRequest.setAdvertisementOwner(ownerId);

        if (ownerId.equals(loggedInUserId)) {
            if (rentRequest.getClient() == null || rentRequest.getClient().equals(loggedInUserId)) {
                throw new InvalidRentRequestDataException("Please choose client for which you create rent request.", HttpStatus.BAD_REQUEST);
            }
            userClient.verify(rentRequest.getClient());
            rentRequest.setRentRequestStatus(RentRequestStatus.PAID);
        } else {
            if (!canCreateRentRequests) {
                throw new InvalidRentRequestDataException("You are not allowed to create rent requests because you canceled your reservations many times. ", HttpStatus.BAD_REQUEST);
            }
            hasDebt(loggedInUserId);
            rentRequest.setClient(loggedInUserId);
            rentRequest.setRentRequestStatus(RentRequestStatus.PENDING);
        }
        rentRequest.setCreatedBy(loggedInUserId);
        rentRequest.setTotalPrice(0.0);
        rentRequest.setRentInfos(new HashSet<>());
        rentRequest.setMessages(new HashSet<>());
        RentRequest savedRequest = rentRequestRepository.save(rentRequest);

        savedRequest = rentRequestRepository.save(setRentInfosData(savedRequest, rentInfos, advertisementDTOS));
        if (savedRequest.getRentRequestStatus().equals(RentRequestStatus.PAID)) {
            rejectOtherRequests(savedRequest);
        } else if (savedRequest.getRentRequestStatus().equals(RentRequestStatus.PENDING)) {
            Instant today = (new Date()).toInstant();
            executeRejectTask(savedRequest.getId(), today.plus(24, ChronoUnit.HOURS));
        }

        return savedRequest;
    }

    private Runnable createRunnable(final Long rentReqId) {
        return () -> {
            RentRequest rentRequest = rentRequestRepository.findOneById(rentReqId);
            if (rentRequest.getRentRequestStatus() == RentRequestStatus.PENDING) {
                rentRequest.setRentRequestStatus(RentRequestStatus.CANCELED);
                rentRequestRepository.save(rentRequest);
                sendRejectedReservation(rentRequest.getClient(), rentRequest.getId());
                logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "SRQ", String.format("Because 24h from creation have passed, rent request %s is automatically CANCELED", rentRequest.getId())));
            }
        };
    }

    @Async
    public void executeRejectTask(Long rentReqId, Instant executionMoment) {
        ScheduledExecutorService localExecutor = Executors.newSingleThreadScheduledExecutor();
        TaskScheduler scheduler = new ConcurrentTaskScheduler(localExecutor);
        scheduler.schedule(createRunnable(rentReqId), executionMoment);
    }

    @Override
    public RentRequestDTO get(Long id, Long loggedInUserId, String jwt, String user) {
        RentRequest rentRequest = rentRequestRepository.findOneById(id);
        if (rentRequest == null) {
            throw new InvalidRentRequestDataException("Requested rent request does not exist.", HttpStatus.NOT_FOUND);
        }
        Set<Long> clientIds = new HashSet<>();
        clientIds.add(rentRequest.getClient());

        Set<Long> advertisements = new HashSet<>();
        for (RentInfo rentInfo : rentRequest.getRentInfos()) {
            advertisements.add(rentInfo.getAdvertisement());
        }
        List<ClientDTO> clientDTOS = userClient.get(jwt, new ArrayList<>(clientIds));
        List<AdvertisementDTO> advertisementDTOS = searchClient.get(jwt, user, new ArrayList<>(advertisements));

        Map<Long, ClientDTO> clientsMap = clientDTOS.stream().collect(Collectors.toMap(ClientDTO::getId, client -> client));
        Map<Long, AdvertisementDTO> advertisementsMap = advertisementDTOS.stream().collect(Collectors.toMap(AdvertisementDTO::getId, adv -> adv));

        return createRentRequestDTO(rentRequest, clientsMap, advertisementsMap, loggedInUserId);
    }

    @Override
    public List<RentRequestDTO> getMine(String status, Long loggedInUserId, String jwt, String user) {
        List<RentRequest> rentRequests;
        if (status.equals("all")) {
            rentRequests = rentRequestRepository.findByClient(loggedInUserId);
        } else {
            rentRequests = rentRequestRepository.findByClientAndRentRequestStatus(loggedInUserId, getRentRequestStatus(status));
        }

        if (rentRequests.isEmpty()) {
            return new ArrayList<>();
        }
        Set<Long> clientIds = new HashSet<>();
        clientIds.add(loggedInUserId);
        Set<Long> advertisements = new HashSet<>();
        for (RentRequest rentRequest : rentRequests) {
            for (RentInfo rentInfo : rentRequest.getRentInfos()) {
                advertisements.add(rentInfo.getAdvertisement());
            }
        }
        List<ClientDTO> clientDTOS = userClient.get(jwt, new ArrayList<>(clientIds));
        List<AdvertisementDTO> advertisementWithIdsDTOS = searchClient.get(jwt, user, new ArrayList<>(advertisements));

        return createListRentRequestDTOs(clientDTOS, advertisementWithIdsDTOS, rentRequests, loggedInUserId);
    }

    @Override
    public List<RentRequestDTO> get(Long advertisementId, String status, Long loggedInUserId, String jwt, String user) {
        List<RentRequest> rentRequests;
        if (status.equals("all")) {
            rentRequests = rentRequestRepository.findByRentInfosAdvertisement(advertisementId);
        } else {
            rentRequests = rentRequestRepository.findByRentInfosAdvertisementAndRentRequestStatus(advertisementId, getRentRequestStatus(status));
        }
        if (rentRequests.isEmpty()) {
            return new ArrayList<>();
        }
        Set<Long> clientIds = new HashSet<>();
        Set<Long> advertisements = new HashSet<>();
        for (RentRequest rentRequest : rentRequests) {
            clientIds.add(rentRequest.getClient());
            for (RentInfo rentInfo : rentRequest.getRentInfos()) {
                advertisements.add(rentInfo.getAdvertisement());
            }
        }
        List<ClientDTO> clientDTOS = userClient.get(jwt, new ArrayList<>(clientIds));
        List<AdvertisementDTO> advertisementWithIdsDTOS = searchClient.get(jwt, user, new ArrayList<>(advertisements));
        return createListRentRequestDTOs(clientDTOS, advertisementWithIdsDTOS, rentRequests, loggedInUserId);
    }

    public RentRequest changeRentRequestStatus(Long id, RentRequestStatusDTO newStatus, Long loggedInUserId) {
        RentRequestStatus rentRequestStatus = getRentRequestStatus(newStatus.getStatus());
        RentRequest rentRequest = get(id, RentRequestStatus.PENDING);

        if (rentRequestStatus.equals(RentRequestStatus.CANCELED)) {
            if (loggedInUserId.equals(rentRequest.getCreatedBy())) {
                return cancel(rentRequest,/**/ loggedInUserId);
            } else {
                return reject(rentRequest, loggedInUserId);
            }
        } else if (rentRequestStatus.equals(RentRequestStatus.PAID)) {
            return accept(rentRequest, loggedInUserId);
        }
        throw new InvalidRentRequestDataException("This rent request's status doesn't exist.", HttpStatus.BAD_REQUEST);
    }

    @Override
    public EditType getAdvertisementEditType(Long advId) {
        List<RentRequest> rentRequests = rentRequestRepository.findByRentInfosAdvertisementAndRentInfosRentRequestRentRequestStatusNot(advId, RentRequestStatus.CANCELED);
        if (rentRequests != null && !rentRequests.isEmpty()) {
            return EditType.PARTIAL;
        }
        return EditType.ALL;
    }

    @Override
    public Boolean canDeleteAdvertisement(Long advId) {
        List<RentRequest> rentRequests = rentRequestRepository.findByRentInfosAdvertisementAndRentInfosRentRequestRentRequestStatusAndRentInfosDateTimeToGreaterThanEqual(advId, RentRequestStatus.PAID, LocalDateTime.now());
        return rentRequests == null || rentRequests.isEmpty();
    }

    @Override
    public Boolean hasRentInfos(List<Long> advIds) {
        for (Long advId : advIds) {
            List<RentRequest> rentRequests = rentRequestRepository.findByRentInfosAdvertisement(advId);
            if (rentRequests != null && !rentRequests.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public RentRequestDTO get(Long id, Long loggedInUserId) {
        return null;
    }

    private RentRequest cancel(RentRequest rentRequest, Long loggedInUserId) {
        rentRequest.setRentRequestStatus(RentRequestStatus.CANCELED);
        RentRequest canceledRentRequest = rentRequestRepository.save(rentRequest);
        sendCancelReservationInfoToUserService(loggedInUserId);
        return canceledRentRequest;
    }

    @Async
    public void sendCancelReservationInfoToUserService(Long loggedInUserId) {
        rentRequestProducer.sendCanceledReservation(loggedInUserId);
    }

    private RentRequest reject(RentRequest rentRequest, Long loggedInUser) {
        if (!loggedInUser.equals(rentRequest.getAdvertisementOwner())) {
            throw new InvalidRentRequestDataException("You aren't owner of rent request's advertisement so you can't reject this request.", HttpStatus.BAD_REQUEST);
        }
        rentRequest.setRentRequestStatus(RentRequestStatus.CANCELED);
        RentRequest rejectedRentRequest = rentRequestRepository.save(rentRequest);
        sendRejectedReservation(rentRequest.getClient(), rentRequest.getId());
        return rejectedRentRequest;
    }

    @Async
    public void sendRejectedReservation(Long clientId, Long rentRequestId) {
        rentRequestProducer.sendRejectedReservation(clientId, rentRequestId);
    }


    private RentRequest accept(RentRequest rentRequest, Long loggedInUser) {
        if (!loggedInUser.equals(rentRequest.getAdvertisementOwner())) {
            throw new InvalidRentRequestDataException("You aren't owner of rent request's advertisement so you can't accept this request.", HttpStatus.BAD_REQUEST);
        }
        checkIfCanAcceptRentRequest(rentRequest);
        rentRequest.setRentRequestStatus(RentRequestStatus.PAID);
        RentRequest paidRentRequest = rentRequestRepository.save(rentRequest);
        rejectOtherRequests(rentRequest);
        sendAcceptedReservation(rentRequest.getClient(), rentRequest.getId());
        return paidRentRequest;
    }

    @Async
    public void sendAcceptedReservation(Long clientId, Long rentRequestId) {
        rentRequestProducer.sendAcceptedReservation(clientId, rentRequestId);
    }

    private List<RentRequestDTO> createListRentRequestDTOs(List<ClientDTO> clientDTOS, List<AdvertisementDTO> advertisementDTOS, List<RentRequest> rentRequests, Long loggedInUserId) {
        Map<Long, ClientDTO> clientsMap = clientDTOS.stream().collect(Collectors.toMap(ClientDTO::getId, client -> client));
        Map<Long, AdvertisementDTO> advertisementsMap = advertisementDTOS.stream().collect(Collectors.toMap(AdvertisementDTO::getId, adv -> adv));

        List<RentRequestDTO> rentRequestDTOS = new ArrayList<>();
        for (RentRequest rentRequest : rentRequests) {
            rentRequestDTOS.add(createRentRequestDTO(rentRequest, clientsMap, advertisementsMap, loggedInUserId));
        }
        return rentRequestDTOS;
    }

    private RentRequestDTO createRentRequestDTO(RentRequest rentRequest, Map<Long, ClientDTO> clientsMap, Map<Long, AdvertisementDTO> advertisementsMap, Long loggedInUserId) {
        RentRequestDTO rentRequestDTO = new RentRequestDTO();
        rentRequestDTO.setId(rentRequest.getId());
        rentRequestDTO.setClient(clientsMap.get(rentRequest.getClient()));
        rentRequestDTO.setTotalPrice(rentRequest.getTotalPrice());
        rentRequestDTO.setRentRequestStatus(rentRequest.getRentRequestStatus().toString());
        Set<RentInfoDTO> rentInfoDTOS = new HashSet<>();
        for (RentInfo rentInfo : rentRequest.getRentInfos()) {
            RentInfoDTO rentInfoDTO = new RentInfoDTO();
            rentInfoDTO.setId(rentInfo.getId());
            rentInfoDTO.setDateTimeFrom(rentInfo.getDateTimeFrom().toString());
            rentInfoDTO.setDateTimeTo(rentInfo.getDateTimeTo().toString());
            rentInfoDTO.setOptedForCDW(rentInfo.getOptedForCDW());
            rentInfoDTO.setAdvertisement(advertisementsMap.get(rentInfo.getAdvertisement()));
            rentInfoDTO.setRating(rentInfo.getRating());
            Set<CommentDTO> commentsDTO = new HashSet<CommentDTO>();
            for (Comment comment : rentInfo.getComments()) {
                commentsDTO.add(commentDtoMapper.toDto(comment));
            }
            rentInfoDTO.setComments(commentsDTO);
            if (rentInfo.getRentReport() != null) {
                rentInfoDTO.setRentReport(rentReportDtoMapper.toDto(rentInfo.getRentReport()));
            }
            rentInfoDTOS.add(rentInfoDTO);
            if (!rentInfoDTO.getAdvertisement().getOwner().getId().equals(loggedInUserId) && !rentRequest.getClient().equals(loggedInUserId)) {
                throw new InvalidRentRequestDataException("You are not allowed to see rent requests of this advertisement.",
                        HttpStatus.NOT_FOUND);
            }

            if (rentInfo.getRentReport() != null && rentInfo.getRentReport().getAdditionalCost() != null) {
                rentInfoDTO.setAdditionalCost(rentInfo.getRentReport().getAdditionalCost());
                rentInfoDTO.setPaid(rentInfo.getRentReport().getPaid());
            }
        }
        rentRequestDTO.setRentInfos(rentInfoDTOS);
        return rentRequestDTO;
    }

    private RentRequestStatus getRentRequestStatus(String status) {
        try {
            return RentRequestStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new InvalidRentRequestDataException("Please choose some of existing rent request's status.",
                    HttpStatus.NOT_FOUND);
        }
    }

    private RentRequest setRentInfosData(RentRequest rentRequest, List<RentInfo> rentInfos, List<AdvertisementWithIdsDTO> advertisementDTOS) throws ParseException {
        double totalPrice = 0;
        Map<Long, AdvertisementWithIdsDTO> advertisementsMap = advertisementDTOS.stream().collect(Collectors.toMap(AdvertisementWithIdsDTO::getId, adv -> adv));

        for (RentInfo rentInfo : rentInfos) {
            rentInfo.setRentRequest(rentRequest);
            AdvertisementWithIdsDTO advertisementDTO = advertisementsMap.get(rentInfo.getAdvertisement());
            LocalDate dateTo = null;
            if (advertisementDTO.getDateTo() != null && !advertisementDTO.getDateTo().isEmpty()) {
                dateTo = getDateConverted(advertisementDTO.getDateTo());
            }
            checkDate(advertisementDTO.getId(), getDateConverted(advertisementDTO.getDateFrom()), dateTo, rentInfo.getDateTimeFrom().toLocalDate(), rentInfo.getDateTimeTo().toLocalDate());
            if (!advertisementDTO.getCDW()) {
                rentInfo.setOptedForCDW(null);
            }
            rentInfo.setCar(advertisementDTO.getCar());
            rentInfo.setRating(0);
            rentInfo.setKilometresLimit(advertisementDTO.getKilometresLimit());
            rentInfo.setPricePerKm(advertisementDTO.getPriceList().getPricePerKm());
            rentInfo.setComments(new HashSet<>());
            rentInfo.setCar(advertisementDTO.getCar());
            totalPrice += countPrice(rentInfo, advertisementDTO);
        }
        rentRequest.setTotalPrice(totalPrice);
        rentRequest.setRentInfos(new HashSet<>(rentInfos));
        return rentRequest;
    }

    @Override
    public void checkDate(Long id, LocalDate advertisementDateFrom, LocalDate advertisementDateTo, LocalDate rentInfoDateFrom, LocalDate rentInfoDateTo) {
        if (rentInfoDateFrom.isBefore(LocalDate.now()) || rentInfoDateTo.isBefore(LocalDate.now())) {
            throw new InvalidRentRequestDataException("Invalid date from/to.", HttpStatus.NOT_FOUND);
        }
        if (rentInfoDateTo.isBefore(rentInfoDateFrom)) {
            throw new InvalidRentRequestDataException("Date To cannot be before Date From.", HttpStatus.NOT_FOUND);
        }
        if (rentInfoDateFrom.isBefore(advertisementDateFrom) || rentInfoDateTo.isBefore(advertisementDateFrom)) {
            throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.", HttpStatus.BAD_REQUEST);
        }

        if (advertisementDateTo != null) {
            if (rentInfoDateFrom.isAfter(advertisementDateTo) || rentInfoDateTo.isAfter(advertisementDateTo)) {
                throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.", HttpStatus.BAD_REQUEST);
            }
        }
        LocalDateTime rentInfoDateTimeFrom = LocalDateTime.of(rentInfoDateFrom.minusDays(1), LocalTime.of(23, 59));
        LocalDateTime rentInfoDateTimeTo = LocalDateTime.of(rentInfoDateTo.plusDays(1), LocalTime.of(0, 0));
        if (!rentRequestRepository.findByRentRequestStatusAndRentInfosDateTimeFromLessThanEqualAndRentInfosDateTimeToGreaterThanAndRentInfosAdvertisement(RentRequestStatus.PAID, rentInfoDateTimeFrom, rentInfoDateTimeFrom, id).isEmpty()) {
            throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.",
                    HttpStatus.BAD_REQUEST);
        }
        if (!rentRequestRepository.findByRentRequestStatusAndRentInfosDateTimeFromLessThanEqualAndRentInfosDateTimeToGreaterThanAndRentInfosAdvertisement(RentRequestStatus.PAID, rentInfoDateTimeTo, rentInfoDateTimeTo, id).isEmpty()) {
            throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.",
                    HttpStatus.BAD_REQUEST);
        }
        if (!rentRequestRepository.findByRentRequestStatusAndRentInfosDateTimeFromGreaterThanEqualAndRentInfosDateTimeToLessThanAndRentInfosAdvertisement(RentRequestStatus.PAID, rentInfoDateTimeFrom, rentInfoDateTimeTo, id).isEmpty()) {
            throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.",
                    HttpStatus.BAD_REQUEST);
        }

    }

    private double countPrice(RentInfo rentInfo, AdvertisementWithIdsDTO advertisementDTO) {
        double price = 0;
        PriceListDTO priceList = advertisementDTO.getPriceList();
        long numberOfHours = ChronoUnit.HOURS.between(rentInfo.getDateTimeFrom(), rentInfo.getDateTimeTo());
        double numberOfDays = Math.ceil(numberOfHours / 24.0);
        if (rentInfo.getOptedForCDW() != null && rentInfo.getOptedForCDW()) {
            price += (numberOfDays * priceList.getPriceForCDW());
        }
        if (numberOfDays > 30 && advertisementDTO.getDiscount() != null) {
            double newPricePerDay = priceList.getPricePerDay()
                    - (priceList.getPricePerDay() * (advertisementDTO.getDiscount() / 100.0));
            price += (numberOfDays * newPricePerDay);
        } else {
            price += (numberOfDays * priceList.getPricePerDay());
        }
        return price;
    }

    private LocalDate getDateConverted(String date) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, formatter);
    }

    private List<AdvertisementWithIdsDTO> getAdvertisements(List<RentInfo> rentInfos) {
        Set<Long> advertisements = new HashSet<>();
        for (RentInfo rentInfo : rentInfos) {
            Long advId = rentInfo.getAdvertisement();
            if (advertisements.contains(advId)) {
                throw new InvalidRentRequestDataException("You cannot choose the same car a couple of time. ", HttpStatus.BAD_REQUEST);
            }
            advertisements.add(rentInfo.getAdvertisement());
        }
        return advertisementClient.get(new ArrayList<>(advertisements));
    }

    private Long getAdvertisementOwnerId(List<AdvertisementWithIdsDTO> advertisementDTOS) {
        Long ownerId = advertisementDTOS.get(0).getOwner();
        for (AdvertisementWithIdsDTO advertisementDTO : advertisementDTOS) {
            if (!advertisementDTO.getOwner().equals(ownerId)) {
                throw new InvalidRentRequestDataException("All advertisements of a rent request must have the same owner.", HttpStatus.BAD_REQUEST);
            }
        }
        return ownerId;
    }

    @Async
    public void rejectOtherRequests(RentRequest rentRequest) {
        for (RentInfo rentInfo : rentRequest.getRentInfos()) {
            LocalDateTime rentInfoDateTimeFrom = rentInfo.getDateTimeFrom();
            LocalDateTime rentInfoDateTimeTo = rentInfo.getDateTimeTo();
            List<RentRequest> rentRequests = rentRequestRepository.findByRentRequestStatusNotAndRentInfosDateTimeFromLessThanEqualAndRentInfosDateTimeToGreaterThanEqualAndRentInfosAdvertisementOrRentRequestStatusNotAndRentInfosDateTimeFromLessThanEqualAndRentInfosDateTimeToGreaterThanEqualAndRentInfosAdvertisementOrRentRequestStatusNotAndRentInfosDateTimeFromGreaterThanEqualAndRentInfosDateTimeToLessThanEqualAndRentInfosAdvertisement(
                    RentRequestStatus.PAID, rentInfoDateTimeFrom, rentInfoDateTimeFrom, rentInfo.getAdvertisement(), RentRequestStatus.PAID, rentInfoDateTimeTo, rentInfoDateTimeTo, rentInfo.getAdvertisement(),
                    RentRequestStatus.PAID, rentInfoDateTimeFrom, rentInfoDateTimeTo, rentInfo.getAdvertisement());

            StringBuilder sb = new StringBuilder();
            for (RentRequest rentRequest1 : rentRequests) {
                rentRequest1.setRentRequestStatus(RentRequestStatus.CANCELED);
                sendRejectedReservation(rentRequest.getClient(), rentRequest.getId());
                sb.append(rentRequest1.getId());
                sb.append(", ");
            }
            if (!rentRequests.isEmpty()) {
                rentRequestRepository.saveAll(rentRequests);
                logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "SRQ", String.format("Because rent request %s is PAID, rent requests %s are REJECTED", rentRequest.getId(), sb.toString())));
            }
        }
    }

    private RentRequest get(Long id, RentRequestStatus status) {
        RentRequest rentRequest = rentRequestRepository.findOneByIdAndRentRequestStatus(id, status);
        if (rentRequest == null) {
            throw new InvalidRentRequestDataException("Requested rent request does not exist.", HttpStatus.NOT_FOUND);
        }
        return rentRequest;
    }

    @Override
    public void checkIfCanAcceptRentRequest(RentRequest rentRequest) {
        for (RentInfo rentInfo : rentRequest.getRentInfos()) {
            LocalDateTime rentInfoDateTimeFrom = LocalDateTime.of(rentInfo.getDateTimeFrom().toLocalDate().minusDays(1), LocalTime.of(23, 59));
            LocalDateTime rentInfoDateTimeTo = LocalDateTime.of(rentInfo.getDateTimeTo().toLocalDate().plusDays(1), LocalTime.of(0, 0));
            if (!rentRequestRepository.findByRentRequestStatusAndRentInfosDateTimeFromLessThanEqualAndRentInfosDateTimeToGreaterThanAndRentInfosAdvertisement(RentRequestStatus.PAID, rentInfoDateTimeFrom, rentInfoDateTimeFrom, rentInfo.getAdvertisement()).isEmpty()) {
                throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.",
                        HttpStatus.BAD_REQUEST);
            }
            if (!rentRequestRepository.findByRentRequestStatusAndRentInfosDateTimeFromLessThanEqualAndRentInfosDateTimeToGreaterThanAndRentInfosAdvertisement(RentRequestStatus.PAID, rentInfoDateTimeTo, rentInfoDateTimeTo, rentInfo.getAdvertisement()).isEmpty()) {
                throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.",
                        HttpStatus.BAD_REQUEST);
            }
            if (!rentRequestRepository.findByRentRequestStatusAndRentInfosDateTimeFromGreaterThanEqualAndRentInfosDateTimeToLessThanAndRentInfosAdvertisement(RentRequestStatus.PAID, rentInfoDateTimeFrom, rentInfoDateTimeTo, rentInfo.getAdvertisement()).isEmpty()) {
                throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.",
                        HttpStatus.BAD_REQUEST);
            }
        }
    }

    @Override
    public RentRequest getRentRequest(Long id) {
        return rentRequestRepository.findOneById(id);
    }

    @Override
    public void hasDebt(Long loggedInUserId) {
        if (!rentRequestRepository.findByRentRequestStatusAndRentInfosRentReportPaidAndClient(RentRequestStatus.PAID, false, loggedInUserId).isEmpty()) {
            throw new InvalidRentRequestDataException("You are not allowed to create rent requests because you have outstanding debts. ", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public List<RentRequest> getAll(Long loggedInUserId) {
        return rentRequestRepository.findAllByAdvertisementOwner(loggedInUserId);
    }

    @Autowired
    public RentRequestServiceImpl(RentRequestRepository rentRequestRepository, AdvertisementClient advertisementClient, UserClient userClient,
                                  SearchClient searchClient, CommentDtoMapper commentDtoMapper,
                                  CommentRepository commentRepository, MessageDtoMapper messageDtoMapper,
                                  RentRequestProducer rentRequestProducer, RentInfoRepository rentInfoRepository,
                                  RentReportDtoMapper rentReportDtoMapper, LogProducer logProducer) {
        this.rentRequestRepository = rentRequestRepository;
        this.advertisementClient = advertisementClient;
        this.userClient = userClient;
        this.searchClient = searchClient;
        this.commentDtoMapper = commentDtoMapper;
        this.commentRepository = commentRepository;
        this.messageDtoMapper = messageDtoMapper;
        this.rentRequestProducer = rentRequestProducer;
        this.rentInfoRepository = rentInfoRepository;
        this.rentReportDtoMapper = rentReportDtoMapper;
        this.logProducer = logProducer;
    }
}
