package jvn.Renting.serviceImpl;

import jvn.Renting.client.AdvertisementClient;
import jvn.Renting.dto.both.AdvertisementWithIdsDTO;
import jvn.Renting.enumeration.RentRequestStatus;
import jvn.Renting.exceptionHandler.InvalidRentReportDataException;
import jvn.Renting.model.RentInfo;
import jvn.Renting.model.RentReport;
import jvn.Renting.producer.RentReportProducer;
import jvn.Renting.repository.RentInfoRepository;
import jvn.Renting.repository.RentReportRepository;
import jvn.Renting.service.RentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RentReportServiceImpl implements RentReportService {

    private RentReportRepository rentReportRepository;

    private RentInfoRepository rentInfoRepository;

    private RentReportProducer rentReportProducer;

    private AdvertisementClient advertisementClient;

    @Override
    public RentReport create(RentReport toEntity, Long rentInfoId) {

        if (rentReportRepository.findByRentInfoId(rentInfoId) != null) {
            throw new InvalidRentReportDataException("Rent report for this rent info already exist.", HttpStatus.BAD_REQUEST);
        }
        //TODO:change mileage to the car and additional cost
        RentInfo rentInfo = rentInfoRepository.findOneById(rentInfoId);
        checkIfCreatingRentReportIsPossible(rentInfo);
        toEntity.setRentInfo(rentInfo);
        rentInfo.setRentReport(toEntity);
        RentReport rentReport = rentReportRepository.save(toEntity);

        sendUpdatesCar(rentReport);
        AdvertisementWithIdsDTO adWithDTO = advertisementClient.getOne(rentReport.getRentInfo().getAdvertisement());
        rentReport.setAdditionalCost(calculateAdditionalCost(rentReport, adWithDTO));
        if (rentReport.getAdditionalCost() > 0) {
            rentReport.setPaid(false);
        } else {
            rentReport.setPaid(true);
        }
        return rentReportRepository.save(rentReport);
    }


    @Async
    public void sendUpdatesCar(RentReport rentReport) {

        rentReportProducer.sendMileage(rentReport);

    }

    @Override
    public void checkIfCreatingRentReportIsPossible(RentInfo rentInfo) {
        if (!rentInfo.getRentRequest().getRentRequestStatus().equals(RentRequestStatus.PAID)) {
            throw new InvalidRentReportDataException("Cannot create rent report if rent request is not paid!", HttpStatus.BAD_REQUEST);
        }
        LocalDateTime date1 = rentInfo.getDateTimeTo();
        LocalDateTime date2 = LocalDateTime.now();

        if ((date2).isBefore(date1)) {
            throw new InvalidRentReportDataException("Cannot create rent report yet! Wait until " + date1.toLocalDate() + "!", HttpStatus.BAD_REQUEST);
        }
        if (rentInfo.getRentReport() != null) {
            throw new InvalidRentReportDataException("There is already rent report linked to this rent info!", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public RentReport getRentReports(Long rentInfoId) {
        RentInfo rentInfo = rentInfoRepository.findOneById(rentInfoId);
        RentReport rentReport = null;
        if (rentInfo.getRentReport() != null) {
            rentReport = rentInfo.getRentReport();
        }
        return rentReport;
    }

    //
//    public void calculateMileageInKm(RentReport rentReport) {
//        Car car = rentReport.getRentInfo().getAdvertisement().getCar();
//
//        Integer previousMileageInKm = car.getMileageInKm();
//        Integer currentMileageInKm = previousMileageInKm + rentReport.getMadeMileage();
//        car.setMileageInKm(currentMileageInKm);
//
//        carRepository.save(car);
//    }
//
    public Double calculateAdditionalCost(RentReport rentReport, AdvertisementWithIdsDTO advertisementWithIdsDTO) {
        Double addCost = 0.0;
        if (advertisementWithIdsDTO.getKilometresLimit() != null) {
            Integer extraMiles = rentReport.getMadeMileage() - advertisementWithIdsDTO.getKilometresLimit();
            if (extraMiles > 0) {
                addCost = extraMiles * advertisementWithIdsDTO.getPriceList().getPricePerKm();
            }
        }
        return addCost;
    }

    @Autowired
    public RentReportServiceImpl(RentReportRepository rentReportRepository, RentInfoRepository rentInfoRepository,
                                 RentReportProducer rentReportProducer, AdvertisementClient advertisementClient) {
        this.rentReportRepository = rentReportRepository;
        this.rentInfoRepository = rentInfoRepository;
        this.rentReportProducer = rentReportProducer;
        this.advertisementClient = advertisementClient;
    }
}
