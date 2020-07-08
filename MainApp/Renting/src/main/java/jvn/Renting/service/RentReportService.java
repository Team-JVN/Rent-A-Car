package jvn.Renting.service;

import jvn.Renting.model.RentInfo;
import jvn.Renting.model.RentReport;

import java.util.List;

public interface RentReportService {

    RentReport create(RentReport toEntity, Long rentInfoId);

    void checkIfCreatingRentReportIsPossible(RentInfo rentInfo);

    RentReport getRentReports(Long rentInfoId);
}
