package jvn.Renting.mapper;

import jvn.Renting.dto.soap.rentreport.RentReportDetails;
import jvn.Renting.model.RentReport;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
public class RentReportDetailsMapper  implements MapperInterface<RentReport, RentReportDetails>{

    private ModelMapper modelMapper;

    @Override
    public RentReport toEntity(RentReportDetails dto){
        RentReport report = new RentReport();
        report.setId(dto.getId());
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
        details.setId(entity.getId());
        details.setPaid(entity.getPaid());
        return details;
    }

    @Autowired
    public RentReportDetailsMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
