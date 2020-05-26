package jvn.Cars.mapper;

import jvn.Cars.dto.both.GearboxTypeDTO;
import jvn.Cars.model.GearboxType;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GearboxTypeDtoMapper implements MapperInterface<GearboxType, GearboxTypeDTO> {

    private ModelMapper modelMapper;

    @Override
    public GearboxType toEntity(GearboxTypeDTO dto) {
        GearboxType entity = modelMapper.map(dto, GearboxType.class);
        return entity;
    }

    @Override
    public GearboxTypeDTO toDto(GearboxType entity) {
        GearboxTypeDTO dto = modelMapper.map(entity, GearboxTypeDTO.class);
        return dto;
    }

    @Autowired
    public GearboxTypeDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}