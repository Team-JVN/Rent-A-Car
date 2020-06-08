package jvn.RentACar.mapper;

import jvn.RentACar.dto.both.RentInfoDTO;
import jvn.RentACar.model.RentInfo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class RentInfoDtoMapper implements MapperInterface<RentInfo, RentInfoDTO> {
    private ModelMapper modelMapper;

    @Override
    public RentInfo toEntity(RentInfoDTO dto) {
        RentInfo rentInfo = new RentInfo();
        rentInfo.setDateTimeFrom(getLocalDateTime(dto.getDateTimeFrom()));
        rentInfo.setDateTimeTo(getLocalDateTime(dto.getDateTimeTo()));
        rentInfo.setOptedForCDW(dto.getOptedForCDW());
        rentInfo.setId(dto.getId());
        return rentInfo;
    }

    @Override
    public RentInfoDTO toDto(RentInfo entity) {
        RentInfoDTO dto = modelMapper.map(entity, RentInfoDTO.class);
        return dto;
    }

    private LocalDateTime getLocalDateTime(String date) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date.substring(0, 10), formatter);
        LocalTime localTime = LocalTime.parse(date.substring(11), DateTimeFormatter.ofPattern("HH:mm"));
        return LocalDateTime.of(localDate, localTime);
    }

    @Autowired
    public RentInfoDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}