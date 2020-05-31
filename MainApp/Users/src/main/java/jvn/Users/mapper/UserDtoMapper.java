package jvn.Users.mapper;

import jvn.Users.dto.response.UserDTO;
import jvn.Users.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper implements MapperInterface<User, UserDTO> {
    public ModelMapper modelMapper;

    @Override
    public User toEntity(UserDTO dto) {
        User user = modelMapper.map(dto, User.class);
        return user;
    }

    @Override
    public UserDTO toDto(User entity) {
        UserDTO dto = modelMapper.map(entity, UserDTO.class);
        return dto;
    }

    @Autowired
    public UserDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
