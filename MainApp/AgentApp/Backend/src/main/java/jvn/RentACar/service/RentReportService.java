package jvn.RentACar.service;
import jvn.RentACar.model.RentReport;

import java.util.List;

public interface RentReportService {

    RentReport create(RentReport rentReport, Long rentInfoId);

    List<RentReport> getAll();

    RentReport get(Long rentInfoId);

    void synchronize();
}


