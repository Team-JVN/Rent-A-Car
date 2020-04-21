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
import jvn.RentACar.service.RentRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

@Service
public class RentRequestServiceImpl implements RentRequestService {

    private ClientService clientService;

    private AdvertisementService advertisementService;

    private RentRequestRepository rentRequestRepository;

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public RentRequest create(RentRequest rentRequest) {

        rentRequest.setClient(clientService.get(rentRequest.getClient().getId()));
        rentRequest.setRentRequestStatus(RentRequestStatus.PAID);
        rentRequest.setTotalPrice(0.0);
        Set<RentInfo> rentInfos = rentRequest.getRentInfos();
        rentRequest.setRentInfos(new HashSet<>());
        RentRequest savedRequest = rentRequestRepository.save(rentRequest);
        rentRequest.setTotalPrice(setRentInfosData(savedRequest, rentInfos, false));
        savedRequest.setRentInfos(rentInfos);
        return rentRequestRepository.save(savedRequest);
    }

    private double setRentInfosData(RentRequest rentRequest, Set<RentInfo> rentInfos, Boolean activeAdvertisement) {
        double totalPrice = 0;
        for (RentInfo rentInfo : rentInfos) {
            rentInfo.setRentRequest(rentRequest);
            Advertisement advertisement = advertisementService.get(rentInfo.getAdvertisement().getId());

            if (!advertisement.getActive()) {
                throw new InvalidRentRequestDataException("This car is already booked.", HttpStatus.NOT_FOUND);
            }

            if (!activeAdvertisement) {
                advertisement.setActive(false);
            }
            checkDate(advertisement, rentInfo.getDateTimeFrom().toLocalDate(), rentInfo.getDateTimeTo().toLocalDate());
            rentInfo.setAdvertisement(advertisement);
            if (!advertisement.getCDW()) {
                rentInfo.setOptedForCDW(null);
            }
            totalPrice += countPrice(rentInfo);
        }
        return totalPrice;
    }

    private void checkDate(Advertisement advertisement, LocalDate rentInfoDateFrom, LocalDate rentInfoDateTo) {
        LocalDate advertisementDateFrom = advertisement.getDateFrom();
        if (rentInfoDateFrom.isBefore(advertisementDateFrom) || rentInfoDateTo.isBefore(advertisementDateFrom)) {
            throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.", HttpStatus.NOT_FOUND);
        }
        LocalDate advertisementDateTo = advertisement.getDateTo();
        if (advertisementDateTo != null) {
            if (rentInfoDateFrom.isAfter(advertisementDateTo) || rentInfoDateTo.isAfter(advertisementDateTo)) {
                throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.", HttpStatus.NOT_FOUND);
            }
        }
    }

    private double countPrice(RentInfo rentInfo) {
        //vidi da li je ok
        double price = 0;
        PriceList priceList = rentInfo.getAdvertisement().getPriceList();
        long numberOfDays = ChronoUnit.DAYS.between(rentInfo.getDateTimeFrom(), rentInfo.getDateTimeTo());
        //uzmi u obzir diskount ako je preslo 30 dana i onda je to popust po danu !!!!
        //price per day * kolikoIma dana
        //price for cdw ako je cekiran
        if (rentInfo.getOptedForCDW() != null && rentInfo.getOptedForCDW()) {
            price += priceList.getPriceForCDW();
        }
        if (numberOfDays > 30 && rentInfo.getAdvertisement().getDiscount() != null) {
            double newPricePerDay = priceList.getPricePerDay() - (priceList.getPricePerDay() * (rentInfo.getAdvertisement().getDiscount() / 100));
            price += (numberOfDays * newPricePerDay);
        } else {
            price += (numberOfDays * priceList.getPricePerDay());
        }
        return price;
    }

    @Autowired
    public RentRequestServiceImpl(ClientService clientService, AdvertisementService advertisementService,
                                  RentRequestRepository rentRequestRepository) {
        this.clientService = clientService;
        this.advertisementService = advertisementService;
        this.rentRequestRepository = rentRequestRepository;
    }
}