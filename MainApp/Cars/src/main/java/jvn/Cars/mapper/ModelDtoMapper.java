package jvn.Cars.mapper;

import jvn.Cars.dto.both.ModelDTO;
import jvn.Cars.model.Model;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ModelDtoMapper implements MapperInterface<Model, ModelDTO> {

    private ModelMapper modelMapper;

    @Override
    public Model toEntity(ModelDTO dto) {
        Model entity = modelMapper.map(dto, Model.class);
        return entity;
    }

    @Override
    public ModelDTO toDto(Model entity) {
        ModelDTO dto = modelMapper.map(entity, ModelDTO.class);
        return dto;
    }

    @Autowired
    public ModelDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}