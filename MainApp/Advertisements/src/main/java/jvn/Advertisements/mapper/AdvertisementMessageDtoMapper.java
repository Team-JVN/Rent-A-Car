package jvn.Advertisements.mapper;

import jvn.Advertisements.dto.message.AdvertisementMessageDTO;
import jvn.Advertisements.model.Advertisement;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class AdvertisementMessageDtoMapper implements MapperInterface<Advertisement, AdvertisementMessageDTO> {

    private ModelMapper modelMapper;

    @Override
    public Advertisement toEntity(AdvertisementMessageDTO dto) throws ParseException {
        Advertisement entity = modelMapper.map(dto, Advertisement.class);
        entity.setDateFrom(getDateConverted(dto.getDateFrom()));
        if (dto.getDateTo() != null) {
            entity.setDateTo(getDateConverted(dto.getDateTo()));
        }
        return entity;
    }

    @Override
    public AdvertisementMessageDTO toDto(Advertisement entity) {
        AdvertisementMessageDTO dto = modelMapper.map(entity, AdvertisementMessageDTO.class);
        dto.setDateFrom(getDateConverted(entity.getDateFrom()));
        if (entity.getDateTo() != null) {
            dto.setDateTo(getDateConverted(entity.getDateTo()));
        }
        return dto;
    }

    public LocalDate getDateConverted(String date) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, formatter);
    }

    public String getDateConverted(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    @Autowired
    public AdvertisementMessageDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}