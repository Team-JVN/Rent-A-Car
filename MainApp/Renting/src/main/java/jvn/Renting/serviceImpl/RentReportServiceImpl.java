package jvn.Renting.serviceImpl;

import jvn.Renting.exceptionHandler.InvalidRentReportDataException;
import jvn.Renting.model.RentReport;
import jvn.Renting.repository.RentReportRepository;
import jvn.Renting.service.RentReportService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class RentReportServiceImpl implements RentReportService {

    private RentReportRepository rentReportRepository;

    @Override
    public RentReport create(RentReport toEntity) {
        if (rentReportRepository.findByRentInfoId(toEntity.getRentInfo().getId()) != null) {
            throw new InvalidRentReportDataException("Rent report for this rent info already exist.", HttpStatus.BAD_REQUEST);
        }
        return rentReportRepository.save(toEntity);
    }
}
