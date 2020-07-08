package jvn.Renting.mapper;

import jvn.Renting.dto.both.RentInfoDTO;
import jvn.Renting.dto.both.RentReportDTO;
import jvn.Renting.model.RentInfo;
import jvn.Renting.model.RentReport;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class RentReportDtoMapper implements MapperInterface<RentReport, RentReportDTO> {

    private ModelMapper modelMapper;

    @Override
    public RentReport toEntity(RentReportDTO dto) {

        RentReport entity = new RentReport();
        entity.setComment(dto.getComment());
        entity.setMadeMileage(dto.getMadeMileage());
        entity.setAdditionalCost(dto.getAdditionalCost());
        return entity;
    }


    @Override
    public RentReportDTO toDto(RentReport entity) {
        RentReportDTO dto = modelMapper.map(entity, RentReportDTO.class);
        return dto;
    }

    private LocalDateTime getLocalDateTime(String date) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date.substring(0, 10), formatter);
        LocalTime localTime = LocalTime.parse(date.substring(11), DateTimeFormatter.ofPattern("HH:mm"));
        return LocalDateTime.of(localDate, localTime);
    }

    @Autowired
    public RentReportDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
