package jvn.RentACar.mapper;

import jvn.RentACar.dto.soap.rentreport.RentReportDetails;
import jvn.RentACar.model.RentReport;
import jvn.RentACar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
public class RentReportDetailsMapper  implements MapperInterface<RentReport, RentReportDetails>{

    private UserService userService;

    @Override
    public RentReport toEntity(RentReportDetails dto) throws ParseException {
        RentReport report = new RentReport();
        report.setId(null);
        report.setMainAppId(dto.getId());
        report.setMadeMileage(dto.getMadeMileage());
        report.setAdditionalCost(dto.getAdditionalCost());
        report.setPaid(dto.isPaid());
        report.setComment(dto.getComment());
        return report;
    }

    @Override
    public RentReportDetails toDto(RentReport entity) {
        RentReportDetails details = new RentReportDetails();
        details.setAdditionalCost(entity.getAdditionalCost());
        details.setMadeMileage(entity.getMadeMileage());
        details.setComment(entity.getComment());
        details.setId(entity.getMainAppId());
        details.setPaid(entity.getPaid());
        return details;
    }

    @Autowired
    public RentReportDetailsMapper(UserService userService) {
        this.userService = userService;
    }
}
