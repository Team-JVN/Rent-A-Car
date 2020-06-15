package jvn.RentACar.mapper;

import jvn.RentACar.dto.soap.pricelist.PriceListDetails;
import jvn.RentACar.model.PriceList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PriceListDetailsMapper implements MapperInterface<PriceList, PriceListDetails> {

    private ModelMapper modelMapper;

    @Override
    public PriceList toEntity(PriceListDetails dto) {
        PriceList entity = modelMapper.map(dto, PriceList.class);
        entity.setPriceForCDW(dto.getPricePerCDW());
        entity.setId(null);
        entity.setMainAppId(dto.getId());
        return entity;
    }

    @Override
    public PriceListDetails toDto(PriceList entity) {
        PriceListDetails dto = modelMapper.map(entity, PriceListDetails.class);
        dto.setPricePerCDW(entity.getPriceForCDW());
        dto.setId(entity.getMainAppId());
        return dto;
    }

    @Autowired
    public PriceListDetailsMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}