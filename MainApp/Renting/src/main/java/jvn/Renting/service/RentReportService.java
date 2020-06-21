package jvn.Renting.service;

import jvn.Renting.model.RentReport;

public interface RentReportService {

    RentReport create(RentReport toEntity, Long rentInfoId);
}
