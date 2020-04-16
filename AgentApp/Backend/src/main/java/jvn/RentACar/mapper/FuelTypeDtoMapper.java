package jvn.RentACar.mapper;

import jvn.RentACar.dto.both.FuelTypeDTO;
import jvn.RentACar.model.FuelType;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FuelTypeDtoMapper implements MapperInterface<FuelType, FuelTypeDTO> {
    public ModelMapper modelMapper;

    @Override
    public FuelType toEntity(FuelTypeDTO dto) {
        FuelType entity = modelMapper.map(dto, FuelType.class);
        return entity;
    }

    @Override
    public FuelTypeDTO toDto(FuelType entity) {
        FuelTypeDTO dto = modelMapper.map(entity, FuelTypeDTO.class);
        return dto;
    }

    @Autowired
    public FuelTypeDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
