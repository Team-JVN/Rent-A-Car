package jvn.RentACar.mapper;

import jvn.RentACar.dto.both.RentInfoDTO;
import jvn.RentACar.dto.both.RentRequestDTO;
import jvn.RentACar.model.Advertisement;
import jvn.RentACar.model.RentInfo;
import jvn.RentACar.model.RentRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RentRequestDtoMapper implements MapperInterface<RentRequest, RentRequestDTO> {
    private ModelMapper modelMapper;

    private ClientDtoMapper clientDtoMapper;

    private AdvertisementWithPicturesDtoMapper advertisementWithPicturesDtoMapper;

    @Override
    public RentRequest toEntity(RentRequestDTO dto)  {
        RentRequest entity = new RentRequest();
        if (dto.getClient() != null) {
            entity.setClient(clientDtoMapper.toEntity(dto.getClient()));
        }
        entity.setTotalPrice(dto.getTotalPrice());
        entity.setId(dto.getId());

        List<RentInfo> entityRentInfos = new ArrayList<>(dto.getRentInfos().size());
        for (RentInfoDTO rentInfoDTO : dto.getRentInfos()) {
            RentInfo rentInfo = new RentInfo();
            rentInfo.setDateTimeFrom(getLocalDateTime(rentInfoDTO.getDateTimeFrom()));
            rentInfo.setDateTimeTo(getLocalDateTime(rentInfoDTO.getDateTimeTo()));
            Advertisement advertisement = new Advertisement();
            advertisement.setId(rentInfoDTO.getAdvertisement().getId());
            rentInfo.setAdvertisement(advertisement);
            rentInfo.setOptedForCDW(rentInfoDTO.getOptedForCDW());
            rentInfo.setId(rentInfoDTO.getId());
            rentInfo.setRating(rentInfoDTO.getRating());
            entityRentInfos.add(rentInfo);
        }
        entity.setRentInfos(new HashSet<>(entityRentInfos));
        return entity;
    }

    @Override
    public RentRequestDTO toDto(RentRequest entity) {
        RentRequestDTO dto = modelMapper.map(entity, RentRequestDTO.class);
        Map<Long, RentInfo> rentInfoMap = entity.getRentInfos().stream().collect(Collectors.toMap(RentInfo::getId, rentInfo -> rentInfo));
        for (RentInfoDTO rentInfoDTO: dto.getRentInfos()) {
            RentInfo rentInfo = rentInfoMap.get(rentInfoDTO.getId());
            if (rentInfo.getRentReport() != null && rentInfo.getRentReport().getAdditionalCost() != null) {
                rentInfoDTO.setAdditionalCost(rentInfo.getRentReport().getAdditionalCost());
                rentInfoDTO.setPaid(rentInfo.getRentReport().getPaid());
            }
        }

        return dto;
    }

    private LocalDateTime getLocalDateTime(String date) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date.substring(0, 10), formatter);
        LocalTime localTime = LocalTime.parse(date.substring(11), DateTimeFormatter.ofPattern("HH:mm"));
        return LocalDateTime.of(localDate, localTime);
    }

    @Autowired
    public RentRequestDtoMapper(ModelMapper modelMapper,ClientDtoMapper clientDtoMapper, AdvertisementWithPicturesDtoMapper advertisementWithPicturesDtoMapper) {
        this.modelMapper = modelMapper;
        this.clientDtoMapper = clientDtoMapper;
        this.advertisementWithPicturesDtoMapper =advertisementWithPicturesDtoMapper;
    }
}