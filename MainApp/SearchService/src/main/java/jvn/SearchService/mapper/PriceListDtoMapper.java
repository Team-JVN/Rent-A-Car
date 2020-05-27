package jvn.SearchService.mapper;

import jvn.SearchService.dto.PriceListDTO;
import jvn.SearchService.model.PriceList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PriceListDtoMapper implements MapperInterface<PriceList, PriceListDTO> {

    private ModelMapper modelMapper;

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