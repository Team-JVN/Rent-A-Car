package jvn.RentACar.serviceImpl;

import jvn.RentACar.enumeration.RentRequestStatus;
import jvn.RentACar.exceptionHandler.InvalidRentRequestDataException;
import jvn.RentACar.model.Advertisement;
import jvn.RentACar.model.PriceList;
import jvn.RentACar.model.RentInfo;
import jvn.RentACar.model.RentRequest;
import jvn.RentACar.repository.RentRequestRepository;
import jvn.RentACar.service.AdvertisementService;
import jvn.RentACar.service.ClientService;
import jvn.RentACar.service.RentInfoService;
import jvn.RentACar.service.RentRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RentRequestServiceImpl implements RentRequestService {

    private ClientService clientService;

    private AdvertisementService advertisementService;

    private RentRequestRepository rentRequestRepository;

    private RentInfoService rentInfoService;

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public RentRequest create(RentRequest rentRequest) {
        // TODO: You need to refuse all other requests
        rentRequest.setClient(clientService.get(rentRequest.getClient().getId()));
        //TODO:Set rentrequeststatus
        //TODO:Set createdByUser
        rentRequest.setRentRequestStatus(RentRequestStatus.PAID);
        rentRequest.setTotalPrice(0.0);
        Set<RentInfo> rentInfos = rentRequest.getRentInfos();
        rentRequest.setRentInfos(new HashSet<>());
        RentRequest savedRequest = rentRequestRepository.save(rentRequest);
        rentRequest.setTotalPrice(setRentInfosData(savedRequest, rentInfos, false));
        savedRequest.setRentInfos(rentInfos);
        return rentRequestRepository.save(savedRequest);
    }

    @Override
    public List<RentRequest> get(String status) {
        if (status.equals("all")) {
            return rentRequestRepository.findAll();
        }
        return rentRequestRepository.findByRentRequestStatus(getRentRequestStatus(status));
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
        if (!rentRequest.getRentRequestStatus().equals(RentRequestStatus.CANCELED)) {
            throw new InvalidRentRequestDataException("This rent request is not CANCELED so you can not delete it.",
                    HttpStatus.FORBIDDEN);
        }
        rentRequest.setClient(null);
        Set<RentInfo> rentInfos = rentRequest.getRentInfos();
        rentRequest.setRentInfos(new HashSet<>());
        rentInfoService.delete(rentInfos);
        rentRequestRepository.deleteById(id);
    }

    private double changeRentInfosData(RentRequest dbRentRequest, RentRequest rentRequest) {
        double totalPrice = 0;
        List<RentInfo> dbRentInfos = new ArrayList<>(dbRentRequest.getRentInfos().size());
        dbRentInfos.addAll(dbRentRequest.getRentInfos());
        Map<Long, RentInfo> rentInfos = rentRequest.getRentInfos().stream()
                .collect(Collectors.toMap(RentInfo::getId, x -> x));

        for (RentInfo dbRentInfo : dbRentInfos) {
            RentInfo rentInfo = rentInfos.get(dbRentInfo.getId());
            if (rentInfo == null) {
                throw new InvalidRentRequestDataException("Please enter valid data.", HttpStatus.BAD_REQUEST);
            }

            Advertisement advertisement = advertisementService.get(dbRentInfo.getAdvertisement().getId());

            checkDate(advertisement, rentInfo.getDateTimeFrom().toLocalDate(), rentInfo.getDateTimeTo().toLocalDate());
            dbRentInfo.setDateTimeFrom(rentInfo.getDateTimeFrom());
            dbRentInfo.setDateTimeTo(rentInfo.getDateTimeTo());

            dbRentInfo.setOptedForCDW(rentInfo.getOptedForCDW());
            if (!advertisement.getCDW()) {
                dbRentInfo.setOptedForCDW(null);
            }
            totalPrice += countPrice(rentInfo);

        }

        return totalPrice;

    }

    private double setRentInfosData(RentRequest rentRequest, Set<RentInfo> rentInfos, Boolean activeAdvertisement) {
        double totalPrice = 0;
        for (RentInfo rentInfo : rentInfos) {
            rentInfo.setRentRequest(rentRequest);
            Advertisement advertisement = advertisementService.get(rentInfo.getAdvertisement().getId());

            checkDate(advertisement, rentInfo.getDateTimeFrom().toLocalDate(), rentInfo.getDateTimeTo().toLocalDate());
            rentInfo.setAdvertisement(advertisement);
            if (!advertisement.getCDW()) {
                rentInfo.setOptedForCDW(null);
            }
            rentInfo.setRating(0);
            rentInfo.setKilometresLimit(advertisement.getKilometresLimit());
            rentInfo.setPricePerKm(advertisement.getPriceList().getPricePerKm());
            totalPrice += countPrice(rentInfo);
        }
        return totalPrice;
    }

    private void checkDate(Advertisement advertisement, LocalDate rentInfoDateFrom, LocalDate rentInfoDateTo) {
        LocalDate advertisementDateFrom = advertisement.getDateFrom();
        if (rentInfoDateFrom.isBefore(advertisementDateFrom) || rentInfoDateTo.isBefore(advertisementDateFrom)) {
            throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.",
                    HttpStatus.NOT_FOUND);
        }
        LocalDate advertisementDateTo = advertisement.getDateTo();
        if (advertisementDateTo != null) {
            if (rentInfoDateFrom.isAfter(advertisementDateTo) || rentInfoDateTo.isAfter(advertisementDateTo)) {
                throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.",
                        HttpStatus.NOT_FOUND);
            }
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
    public RentRequestServiceImpl(ClientService clientService, AdvertisementService advertisementService,
                                  RentRequestRepository rentRequestRepository, RentInfoService rentInfoService) {
        this.clientService = clientService;
        this.advertisementService = advertisementService;
        this.rentRequestRepository = rentRequestRepository;
        this.rentInfoService = rentInfoService;
    }
}
