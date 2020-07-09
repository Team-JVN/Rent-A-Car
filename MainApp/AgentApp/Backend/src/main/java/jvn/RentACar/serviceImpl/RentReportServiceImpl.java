package jvn.RentACar.serviceImpl;


import jvn.RentACar.client.RentReportClient;
import jvn.RentACar.dto.soap.rentreport.CheckIfCanCreateRentReportResponse;
import jvn.RentACar.dto.soap.rentreport.CreateRentReportResponse;
import jvn.RentACar.dto.soap.rentreport.GetAllRentReportsDetailsResponse;
import jvn.RentACar.dto.soap.rentreport.RentReportDetails;
import jvn.RentACar.enumeration.RentRequestStatus;
import jvn.RentACar.exceptionHandler.InvalidAdvertisementDataException;
import jvn.RentACar.exceptionHandler.InvalidCommentDataException;
import jvn.RentACar.mapper.RentReportDetailsMapper;
import jvn.RentACar.model.Car;
import jvn.RentACar.model.Log;
import jvn.RentACar.model.RentInfo;
import jvn.RentACar.model.RentReport;
import jvn.RentACar.repository.CarRepository;
import jvn.RentACar.repository.RentInfoRepository;
import jvn.RentACar.repository.RentReportRepository;
import jvn.RentACar.service.LogService;
import jvn.RentACar.service.RentInfoService;
import jvn.RentACar.service.RentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RentReportServiceImpl implements RentReportService {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private RentReportRepository rentReportRepository;

    private RentInfoService rentInfoService;

    private CarRepository carRepository;

    private RentReportClient rentReportClient;

    private RentInfoRepository rentInfoRepository;

    private RentReportDetailsMapper rentReportDetailsMapper;

    private LogService logService;

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public RentReport create(RentReport rentReport, Long rentInfoId) {
        RentInfo rentInfo = rentInfoService.get(rentInfoId);
        rentReport.setRentInfo(rentInfo);
//        checkIfCreatingRentReportIsPossible(rentInfoService.get(rentInfoId));
        CheckIfCanCreateRentReportResponse response = rentReportClient.checkIfCanCreateRentReport(rentInfo.getMainAppId());

        if (response == null || !response.isValue()) {


            throw new InvalidCommentDataException("Cannot create rent report.",
                    HttpStatus.BAD_REQUEST);
        }
        rentReport.setAdditionalCost(calculateAdditionalCost(rentReport));
        if (rentReport.getAdditionalCost() > 0) {
            rentReport.setPaid(false);
        } else {
            rentReport.setPaid(true);
        }
        calculateMileageInKm(rentReport);
        rentReport = rentReportRepository.save(rentReport);
        CreateRentReportResponse createRentReportResponse = rentReportClient.createRentReport(rentInfo.getMainAppId(), rentReport);
        RentReportDetails rentReportDetails = createRentReportResponse.getRentReportDetails();
        if (rentReportDetails != null && rentReportDetails.getId() != null) {

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


    @Scheduled(cron = "0 40 0/3 * * ?")
    public void synchronizeRentReports() {
        try {
            for (RentInfo rentInfo : rentInfoRepository.findAll()) {
                GetAllRentReportsDetailsResponse response = rentReportClient.getRentReports(rentInfo.getMainAppId());
                if (response == null) {
                    continue;
                } else {
                    RentReportDetails rentReportDetails = response.getRentReportDetails();
                    if (rentReportDetails == null) {
                        continue;
                    } else {
                        RentReport rentReport = rentReportDetailsMapper.toEntity(rentReportDetails);
                        RentReport dbRentReport = rentReportRepository.findByMainAppId(rentReport.getMainAppId());
                        if (dbRentReport == null) {
                            createSynchronize(rentReport, rentInfo);
                        } else {
                            editSynchronize(rentReport, dbRentReport, rentInfo);
                        }

                        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "SYN",
                                "[SOAP] Rent reports are successfully synchronized"));
                    }


                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void createSynchronize(RentReport rentReport, RentInfo rentInfo) {
        try {
            rentReport.setRentInfo(rentInfo);
            rentReportRepository.saveAndFlush(rentReport);
        } catch (DataIntegrityViolationException ex) {

        }

    }

    private void editSynchronize(RentReport rentReport, RentReport dbRentReport, RentInfo rentInfo) {
        dbRentReport.setComment(rentReport.getComment());
        dbRentReport.setPaid(rentReport.getPaid());
        dbRentReport.setAdditionalCost(rentReport.getAdditionalCost());
        dbRentReport.setMadeMileage(rentReport.getMadeMileage());
        dbRentReport.setRentInfo(rentInfo);
        rentReportRepository.saveAndFlush(dbRentReport);
    }

    @Override
    public List<RentReport> getAll() {
        return rentReportRepository.findAll();

    }

    @Override
    public RentReport get(Long rentInfoId) {
        RentInfo rentInfo = rentInfoService.get(rentInfoId);
        RentReport rentReport = rentInfo.getRentReport();
        return rentReport;
    }

    @Override
    public void synchronize() {
        synchronizeRentReports();
    }

    @Autowired
    public RentReportServiceImpl(RentReportRepository rentReportRepository, RentInfoService rentInfoService,
                                 CarRepository carRepository, RentReportClient rentReportClient, LogService logService,
                                 RentReportDetailsMapper rentReportDetailsMapper, RentInfoRepository rentInfoRepository) {
        this.rentReportRepository = rentReportRepository;
        this.rentInfoService = rentInfoService;
        this.carRepository = carRepository;
        this.rentReportClient = rentReportClient;
        this.logService = logService;
        this.rentReportDetailsMapper = rentReportDetailsMapper;
        this.rentInfoRepository = rentInfoRepository;
    }
}
