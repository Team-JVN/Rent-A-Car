package jvn.RentACar.mapper;

import jvn.RentACar.dto.both.PriceListDTO;
import jvn.RentACar.model.PriceList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PriceListDtoMapper implements MapperInterface<PriceList, PriceListDTO> {
    public ModelMapper modelMapper;

    @Override
    public PriceList toEntity(PriceListDTO dto) {
        PriceList entity = modelMapper.map(dto, PriceList.class);
        return entity;
    }

    @Override
    public PriceListDTO toDto(PriceList entity) {
        PriceListDTO dto = modelMapper.map(entity, PriceListDTO.class);
        return dto;
    }

    @Autowired
    public PriceListDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}