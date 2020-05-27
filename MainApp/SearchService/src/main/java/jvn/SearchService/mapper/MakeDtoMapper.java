package jvn.SearchService.mapper;

import jvn.SearchService.dto.MakeDTO;
import jvn.SearchService.model.Make;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MakeDtoMapper implements MapperInterface<Make, MakeDTO> {

    private ModelMapper modelMapper;

    @Override
    public Make toEntity(MakeDTO dto) {
        Make entity = modelMapper.map(dto, Make.class);
        return entity;
    }

    @Override
    public MakeDTO toDto(Make entity) {
        MakeDTO dto = modelMapper.map(entity, MakeDTO.class);
        return dto;
    }

    @Autowired
    public MakeDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}