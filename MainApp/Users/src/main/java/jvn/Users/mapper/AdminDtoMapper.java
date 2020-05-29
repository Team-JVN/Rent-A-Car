package jvn.Users.mapper;

import jvn.Users.dto.both.AdminDTO;
import jvn.Users.model.Admin;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
@Component
public class AdminDtoMapper implements MapperInterface<Admin, AdminDTO>{

    public ModelMapper modelMapper;

    @Override
    public Admin toEntity(AdminDTO dto) throws ParseException {
       Admin admin = modelMapper.map(dto, Admin.class);
       return admin;
    }

    @Override
    public AdminDTO toDto(Admin entity) {
        AdminDTO adminDTO = modelMapper.map(entity, AdminDTO.class);
        return adminDTO;
    }

    @Autowired
    public AdminDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
