package jvn.RentACar.mapper;

import jvn.RentACar.dto.response.AdvertisementWithPicturesDTO;
import jvn.RentACar.model.Advertisement;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class AdvertisementWithPicturesDtoMapper implements MapperInterface<Advertisement, AdvertisementWithPicturesDTO> {

    private ModelMapper modelMapper;

    @Override
    public Advertisement toEntity(AdvertisementWithPicturesDTO dto) throws ParseException {
        dto.getCar().setOwner(null);
        Advertisement entity = modelMapper.map(dto, Advertisement.class);
        entity.setDateFrom(getDateConverted(dto.getDateFrom()));
        if (dto.getDateTo() != null) {
            entity.setDateTo(getDateConverted(dto.getDateTo()));
        }
        return entity;
    }

    @Override
    public AdvertisementWithPicturesDTO toDto(Advertisement entity) {
        AdvertisementWithPicturesDTO dto = modelMapper.map(entity, AdvertisementWithPicturesDTO.class);
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
    public AdvertisementWithPicturesDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
