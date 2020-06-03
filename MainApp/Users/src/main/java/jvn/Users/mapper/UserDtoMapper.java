package jvn.Users.mapper;

import jvn.Users.dto.response.UserDTO;
import jvn.Users.model.Client;
import jvn.Users.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
        dto.setRole(entity.getRole().getName());
        if (entity instanceof Client) {
            Client client = (Client) entity;
            dto.setCanCreateComments(client.getCanCreateComments());
            dto.setCanCreateRentRequests(client.getCanCreateRentRequests());
        }
        List<String> permissions = new ArrayList<>();
        entity.getRole().getPermissions().forEach(p -> {
            permissions.add(p.getName());
        });
        dto.setPermissions(permissions);
        return dto;
    }

    @Autowired
    public UserDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
