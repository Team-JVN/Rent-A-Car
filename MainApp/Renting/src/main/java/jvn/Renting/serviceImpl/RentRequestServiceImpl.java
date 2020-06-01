package jvn.Renting.serviceImpl;

import jvn.Renting.client.AdvertisementClient;
import jvn.Renting.client.UserClient;
import jvn.Renting.dto.both.AdvertisementDTO;
import jvn.Renting.dto.both.PriceListDTO;
import jvn.Renting.enumeration.RentRequestStatus;
import jvn.Renting.exceptionHandler.InvalidRentRequestDataException;
import jvn.Renting.model.RentInfo;
import jvn.Renting.model.RentRequest;
import jvn.Renting.repository.RentRequestRepository;
import jvn.Renting.service.RentRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class RentRequestServiceImpl implements RentRequestService {

    private RentRequestRepository rentRequestRepository;

    private AdvertisementClient advertisementClient;

    private UserClient userClient;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public RentRequest create(RentRequest rentRequest, Long loggedInUserId) throws ParseException {
        List<RentInfo> rentInfos = new ArrayList<>(rentRequest.getRentInfos());
        List<AdvertisementDTO> advertisementDTOS = getAdvertisements(rentInfos);
        Long ownerId = advertisementOwnerId(advertisementDTOS);

        if (ownerId.equals(loggedInUserId)) {
            if (rentRequest.getClient().equals(loggedInUserId)) {
                throw new InvalidRentRequestDataException("Please choose client for which you create rent request.", HttpStatus.BAD_REQUEST);
            }
            userClient.verify(rentRequest.getClient());
            rentRequest.setRentRequestStatus(RentRequestStatus.PAID);
        } else {
            rentRequest.setClient(loggedInUserId);
            rentRequest.setRentRequestStatus(RentRequestStatus.PENDING);
        }
        rentRequest.setCreatedBy(loggedInUserId);
        rentRequest.setTotalPrice(0.0);
        rentRequest.setRentInfos(new HashSet<>());
        RentRequest savedRequest = rentRequestRepository.save(rentRequest);

        savedRequest = rentRequestRepository.save(setRentInfosData(savedRequest, rentInfos, advertisementDTOS));
        if (savedRequest.getRentRequestStatus().equals(RentRequestStatus.PAID)) {
            //TODO: REJECT OTHER REQUESTS!!!!
            rejectOtherRequests(savedRequest);
        }

        return savedRequest;
    }

    private RentRequest setRentInfosData(RentRequest rentRequest, List<RentInfo> rentInfos, List<AdvertisementDTO> advertisementDTOS) throws ParseException {
        double totalPrice = 0;
        int i = 0;
        for (RentInfo rentInfo : rentInfos) {
            rentInfo.setRentRequest(rentRequest);
            AdvertisementDTO advertisementDTO = advertisementDTOS.get(i);
            checkDate(advertisementDTO, rentInfo.getDateTimeFrom().toLocalDate(), rentInfo.getDateTimeTo().toLocalDate());
            if (!advertisementDTO.getCDW()) {
                rentInfo.setOptedForCDW(null);
            }
            rentInfo.setRating(0);
            rentInfo.setKilometresLimit(advertisementDTO.getKilometresLimit());
            rentInfo.setPricePerKm(advertisementDTO.getPriceList().getPricePerKm());
            totalPrice += countPrice(rentInfo, advertisementDTO);
            i++;
        }
        rentRequest.setTotalPrice(totalPrice);
        rentRequest.setRentInfos(new HashSet<>(rentInfos));
        return rentRequest;
    }


    private void checkDate(AdvertisementDTO advertisement, LocalDate rentInfoDateFrom, LocalDate rentInfoDateTo) throws ParseException {
        LocalDate advertisementDateFrom = getDateConverted(advertisement.getDateFrom());
        if (rentInfoDateFrom.isBefore(LocalDate.now()) || rentInfoDateTo.isBefore(LocalDate.now())) {
            throw new InvalidRentRequestDataException("Invalid date from/to.", HttpStatus.NOT_FOUND);
        }
        if (rentInfoDateTo.isBefore(rentInfoDateFrom)) {
            throw new InvalidRentRequestDataException("Date To cannot be before Date From.", HttpStatus.NOT_FOUND);
        }
        if (rentInfoDateFrom.isBefore(advertisementDateFrom) || rentInfoDateTo.isBefore(advertisementDateFrom)) {
            throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.", HttpStatus.BAD_REQUEST);
        }

        if (advertisement.getDateTo() != null && !advertisement.getDateTo().isEmpty()) {
            LocalDate advertisementDateTo = getDateConverted(advertisement.getDateTo());
            if (rentInfoDateFrom.isAfter(advertisementDateTo) || rentInfoDateTo.isAfter(advertisementDateTo)) {
                throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.", HttpStatus.BAD_REQUEST);
            }
        }
        LocalDateTime rentInfoDateTimeFrom = LocalDateTime.of(rentInfoDateFrom.minusDays(1), LocalTime.of(23, 59));
        LocalDateTime rentInfoDateTimeTo = LocalDateTime.of(rentInfoDateTo.plusDays(1), LocalTime.of(0, 0));
        if (!rentRequestRepository.findByRentRequestStatusAndRentInfosDateTimeFromLessThanEqualAndRentInfosDateTimeToGreaterThanEqual(RentRequestStatus.PAID, rentInfoDateTimeFrom, rentInfoDateTimeFrom).isEmpty()) {
            throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.",
                    HttpStatus.BAD_REQUEST);
        }
        if (!rentRequestRepository.findByRentRequestStatusAndRentInfosDateTimeFromLessThanEqualAndRentInfosDateTimeToGreaterThanEqual(RentRequestStatus.PAID, rentInfoDateTimeTo, rentInfoDateTimeTo).isEmpty()) {
            throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.",
                    HttpStatus.BAD_REQUEST);
        }
        if (!rentRequestRepository.findByRentRequestStatusAndRentInfosDateTimeFromGreaterThanEqualAndRentInfosDateTimeToLessThanEqual(RentRequestStatus.PAID, rentInfoDateTimeFrom, rentInfoDateTimeTo).isEmpty()) {
            throw new InvalidRentRequestDataException("Chosen car is not available at specified date and time.",
                    HttpStatus.BAD_REQUEST);
        }

    }

    private double countPrice(RentInfo rentInfo, AdvertisementDTO advertisementDTO) {
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

    private List<AdvertisementDTO> getAdvertisements(List<RentInfo> rentInfos) {
        List<Long> advertisements = new ArrayList<>();
        for (RentInfo rentInfo : rentInfos) {
            advertisements.add(rentInfo.getAdvertisement());
        }
        return advertisementClient.get(advertisements);
    }

    private Long advertisementOwnerId(List<AdvertisementDTO> advertisementDTOS) {
        Long ownerId = advertisementDTOS.get(0).getOwner();
        for (AdvertisementDTO advertisementDTO : advertisementDTOS) {
            if (!advertisementDTO.getOwner().equals(ownerId)) {
                throw new InvalidRentRequestDataException("All advertisements of a rent request must have the same owner.", HttpStatus.BAD_REQUEST);
            }
        }
        return ownerId;
    }

    private void rejectOtherRequests(RentRequest rentRequest) {
        for (RentInfo rentInfo : rentRequest.getRentInfos()) {
            List<RentRequest> rentRequests = rentRequestRepository.findByRentRequestStatusNotAndRentInfosAdvertisement(RentRequestStatus.PAID, rentInfo.getAdvertisement());
            for (RentRequest rentRequest1 : rentRequests) {
                rentRequest1.setRentRequestStatus(RentRequestStatus.CANCELED);
            }
            if (!rentRequests.isEmpty()) {
                rentRequestRepository.saveAll(rentRequests);
            }
        }
    }

    @Autowired
    public RentRequestServiceImpl(RentRequestRepository rentRequestRepository, AdvertisementClient advertisementClient, UserClient userClient) {
        this.rentRequestRepository = rentRequestRepository;
        this.advertisementClient = advertisementClient;
        this.userClient = userClient;
    }
}
