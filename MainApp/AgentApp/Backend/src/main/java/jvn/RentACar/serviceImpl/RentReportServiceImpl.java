package jvn.RentACar.serviceImpl;


import jvn.RentACar.client.RentReportClient;
import jvn.RentACar.dto.soap.rentreport.CreateRentReportResponse;
import jvn.RentACar.dto.soap.rentreport.RentReportDetails;
import jvn.RentACar.enumeration.RentRequestStatus;
import jvn.RentACar.exceptionHandler.InvalidAdvertisementDataException;
import jvn.RentACar.model.Car;
import jvn.RentACar.model.RentInfo;
import jvn.RentACar.model.RentReport;
import jvn.RentACar.repository.CarRepository;
import jvn.RentACar.repository.RentReportRepository;
import jvn.RentACar.service.RentInfoService;
import jvn.RentACar.service.RentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RentReportServiceImpl implements RentReportService {

    private RentReportRepository rentReportRepository;

    private RentInfoService rentInfoService;

    private CarRepository carRepository;

    private RentReportClient rentReportClient;

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public RentReport create(RentReport rentReport, Long rentInfoId) {
        RentInfo rentInfo = rentInfoService.get(rentInfoId);
        rentReport.setRentInfo(rentInfo);
        checkIfCreatingRentReportIsPossible(rentInfoService.get(rentInfoId));
        rentReport.setAdditionalCost(calculateAdditionalCost(rentReport));
        calculateMileageInKm(rentReport);
        rentReport = rentReportRepository.save(rentReport);
        CreateRentReportResponse createRentReportResponse = rentReportClient.createRentReport(rentInfo.getMainAppId(), rentReport);
        RentReportDetails rentReportDetails = createRentReportResponse.getRentReportDetails();
        if(rentReportDetails != null && rentReportDetails.getId() != null){

            rentReport.setMainAppId(rentReportDetails.getId());
        }
        return rentReport;

    }

    public void checkIfCreatingRentReportIsPossible(RentInfo rentInfo) {

        if (!rentInfo.getRentRequest().getRentRequestStatus().equals(RentRequestStatus.PAID)) {
            throw new InvalidAdvertisementDataException("Cannot create rent report if rent request is not paid!", HttpStatus.BAD_REQUEST);
        }
        LocalDateTime date1 = rentInfo.getDateTimeTo();
        LocalDateTime date2 = LocalDateTime.now();

        if ((date2).isBefore(date1)) {
            throw new InvalidAdvertisementDataException("Cannot create rent report yet! Wait until " + date1.toLocalDate() + "!", HttpStatus.BAD_REQUEST);
        }
        if (rentInfo.getRentReport() != null) {
            throw new InvalidAdvertisementDataException("There is already rent report linked to this rent info!", HttpStatus.BAD_REQUEST);
        }
    }

    public Double calculateAdditionalCost(RentReport rentReport) {

        Double addCost = 0.0;
        if (rentReport.getRentInfo().getAdvertisement().getKilometresLimit() != null) {
            Integer extraMiles = rentReport.getMadeMileage() - rentReport.getRentInfo().getAdvertisement().getKilometresLimit();
            if (extraMiles > 0) {
                addCost = extraMiles * rentReport.getRentInfo().getAdvertisement().getPriceList().getPricePerKm();
            }
        }
        return addCost;
    }

    public void calculateMileageInKm(RentReport rentReport) {

        Car car = rentReport.getRentInfo().getAdvertisement().getCar();

        Integer previousMileageInKm = car.getMileageInKm();
        Integer currentMileageInKm = previousMileageInKm + rentReport.getMadeMileage();
        car.setMileageInKm(currentMileageInKm);

        carRepository.save(car);
    }

    @Override
    public List<RentReport> getAll() {
        return rentReportRepository.findAll();

    }

    @Autowired
    public RentReportServiceImpl(RentReportRepository rentReportRepository, RentInfoService rentInfoService,
                                 CarRepository carRepository, RentReportClient rentReportClient) {
        this.rentReportRepository = rentReportRepository;
        this.rentInfoService = rentInfoService;
        this.carRepository = carRepository;
        this.rentReportClient = rentReportClient;
    }
}
