package jvn.RentACar.mapper;

import jvn.RentACar.dto.both.RentInfoDTO;
import jvn.RentACar.dto.both.RentRequestDTO;
import jvn.RentACar.model.RentInfo;
import jvn.RentACar.model.RentRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class RentRequestDtoMapper implements MapperInterface<RentRequest, RentRequestDTO> {
    private ModelMapper modelMapper;

    @Override
    public RentRequest toEntity(RentRequestDTO dto) {
        RentRequest entity = modelMapper.map(dto, RentRequest.class);
        List<RentInfo> entityRentInfos = new ArrayList<>(entity.getRentInfos().size());
        entityRentInfos.addAll(entity.getRentInfos());

        List<RentInfoDTO> rentInfos = new ArrayList<>(dto.getRentInfos().size());
        rentInfos.addAll(dto.getRentInfos());

        for (int i = 0; i < entityRentInfos.size(); i++) {
            RentInfoDTO rentInfoDTO = rentInfos.get(i);

            RentInfo rentInfo = entityRentInfos.get(i);
            rentInfo.setDateTimeFrom(getLocalDateTime(rentInfoDTO.getDateTimeFrom()));
            rentInfo.setDateTimeTo(getLocalDateTime(rentInfoDTO.getDateTimeTo()));
        }

        return entity;
    }

    @Override
    public RentRequestDTO toDto(RentRequest entity) {
        RentRequestDTO dto = modelMapper.map(entity, RentRequestDTO.class);
        return dto;
    }

    private LocalDateTime getLocalDateTime(String date) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate = LocalDate.parse(date.substring(6), formatter);
        LocalTime localTime = LocalTime.parse(date.substring(0, 5), DateTimeFormatter.ofPattern("HH:mm"));
        return LocalDateTime.of(localDate, localTime);
    }

    @Autowired
    public RentRequestDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}