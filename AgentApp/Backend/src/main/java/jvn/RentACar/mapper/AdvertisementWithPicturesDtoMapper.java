package jvn.RentACar.mapper;

import jvn.RentACar.dto.response.AdvertisementWithPicturesDTO;
import jvn.RentACar.model.Advertisement;
import jvn.RentACar.model.Picture;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class AdvertisementWithPicturesDtoMapper implements MapperInterface<Advertisement, AdvertisementWithPicturesDTO> {

    private ModelMapper modelMapper;

    @Override
    public Advertisement toEntity(AdvertisementWithPicturesDTO dto) {
        Advertisement entity = modelMapper.map(dto, Advertisement.class);
        return entity;
    }

    @Override
    public AdvertisementWithPicturesDTO toDto(Advertisement entity) {
        AdvertisementWithPicturesDTO dto = modelMapper.map(entity, AdvertisementWithPicturesDTO.class);
        dto.setPictures(getPicturesConverted(entity.getCar().getPictures()));
        return dto;
    }

    private List<String> getPicturesConverted(Set<Picture> pictures) {
        List<String> pictureNames = new ArrayList<>();
        for (Picture picture : pictures) {
            pictureNames.add(picture.getData());
        }
        return pictureNames;
    }

    @Autowired
    public AdvertisementWithPicturesDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
