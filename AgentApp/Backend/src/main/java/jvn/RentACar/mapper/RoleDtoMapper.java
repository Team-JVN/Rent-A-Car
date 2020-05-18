package jvn.RentACar.mapper;


import jvn.RentACar.dto.both.RoleDTO;
import jvn.RentACar.model.Role;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoleDtoMapper implements MapperInterface<Role, RoleDTO> {

    private ModelMapper modelMapper;

    @Override
    public Role toEntity(RoleDTO dto) {
        Role entity = modelMapper.map(dto, Role.class);
        return entity;
    }

    @Override
    public RoleDTO toDto(Role entity) {
        RoleDTO dto = modelMapper.map(entity, RoleDTO.class);
        return dto;
    }

    @Autowired
    public RoleDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
