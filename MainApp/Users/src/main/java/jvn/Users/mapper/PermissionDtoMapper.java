package jvn.Users.mapper;

import jvn.Users.dto.both.PermissionDTO;
import jvn.Users.model.Permission;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
public class PermissionDtoMapper implements MapperInterface<Permission, PermissionDTO> {

    private ModelMapper modelMapper;

    @Override
    public Permission toEntity(PermissionDTO dto) throws ParseException {
        Permission entity = modelMapper.map(dto, Permission.class);
        return entity;
    }

    @Override
    public PermissionDTO toDto(Permission entity) {
        PermissionDTO dto = modelMapper.map(entity, PermissionDTO.class);
        return dto;
    }

    @Autowired
    public PermissionDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}

