package jvn.Users.mapper;

import jvn.Users.dto.both.BodyStyleDTO;
import jvn.Users.model.BodyStyle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BodyStyleDtoMapper implements MapperInterface<BodyStyle, BodyStyleDTO> {

    private ModelMapper modelMapper;

    @Override
    public BodyStyle toEntity(BodyStyleDTO dto) {
        BodyStyle entity = modelMapper.map(dto, BodyStyle.class);
        return entity;
    }

    @Override
    public BodyStyleDTO toDto(BodyStyle entity) {
        BodyStyleDTO dto = modelMapper.map(entity, BodyStyleDTO.class);
        return dto;
    }

    @Autowired
    public BodyStyleDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
