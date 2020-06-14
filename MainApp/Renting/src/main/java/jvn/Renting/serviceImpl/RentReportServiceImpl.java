package jvn.Renting.serviceImpl;

import jvn.Renting.exceptionHandler.InvalidRentReportDataException;
import jvn.Renting.model.RentReport;
import jvn.Renting.repository.RentReportRepository;
import jvn.Renting.service.RentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RentReportServiceImpl implements RentReportService {

    private RentReportRepository rentReportRepository;

    @Override
    public RentReport create(RentReport toEntity) {
        if (rentReportRepository.findByRentInfoId(toEntity.getRentInfo().getId()) != null) {
            throw new InvalidRentReportDataException("Rent report for this rent info already exist.", HttpStatus.BAD_REQUEST);
        }
        //TODO:change mileage to the car and additional cost
        return rentReportRepository.save(toEntity);
    }

//    public void checkIfCreatingRentReportIsPossible(RentInfo rentInfo) {
//        if (!rentInfo.getRentRequest().getRentRequestStatus().equals(RentRequestStatus.PAID)) {
//            throw new InvalidAdvertisementDataException("Cannot create rent report if rent request is not paid!", HttpStatus.BAD_REQUEST);
//        }
//        LocalDateTime date1 = rentInfo.getDateTimeTo();
//        LocalDateTime date2 = LocalDateTime.now();
//
//        if ((date2).isBefore(date1)) {
//            throw new InvalidAdvertisementDataException("Cannot create rent report yet! Wait until " + date1.toLocalDate() + "!", HttpStatus.BAD_REQUEST);
//        }
//        if (rentInfo.getRentReport() != null) {
//            throw new InvalidAdvertisementDataException("There is already rent report linked to this rent info!", HttpStatus.BAD_REQUEST);
//        }
//    }
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
//    public Double calculateAdditionalCost(RentReport rentReport) {
//        Double addCost = 0.0;
//        if (rentReport.getRentInfo().getAdvertisement().getKilometresLimit() != null) {
//            Integer extraMiles = rentReport.getMadeMileage() - rentReport.getRentInfo().getAdvertisement().getKilometresLimit();
//            if (extraMiles > 0) {
//                addCost = extraMiles * rentReport.getRentInfo().getAdvertisement().getPriceList().getPricePerKm();
//            }
//        }
//        return addCost;
//    }

    @Autowired
    public RentReportServiceImpl(RentReportRepository rentReportRepository) {
        this.rentReportRepository = rentReportRepository;
    }
}
