package jvn.RentACar.mapper;

import jvn.RentACar.dto.both.AdvertisementDTO;
import jvn.RentACar.model.Advertisement;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class AdvertisementDtoMapper implements MapperInterface<Advertisement, AdvertisementDTO> {

    private ModelMapper modelMapper;

    @Override
    public Advertisement toEntity(AdvertisementDTO dto) throws ParseException {
        Advertisement entity = modelMapper.map(dto, Advertisement.class);
        entity.setDateFrom(getDateConverted(dto.getDateFrom()));
        return entity;
    }

    @Override
    public AdvertisementDTO toDto(Advertisement entity) {
        AdvertisementDTO dto = modelMapper.map(entity, AdvertisementDTO.class);
        dto.setDateFrom(getDateConverted(entity.getDateFrom()));
        return dto;
    }

    public LocalDate getDateConverted(String date) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(date, formatter);
    }

    public String getDateConverted(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    @Autowired
    public AdvertisementDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}