package jvn.RentACar.mapper;

import jvn.RentACar.dto.both.ClientDTO;
import jvn.RentACar.dto.both.CommentDTO;
import jvn.RentACar.dto.both.UserDTO;
import jvn.RentACar.model.Comment;
import jvn.RentACar.model.User;
import jvn.RentACar.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentDtoMapper implements MapperInterface<Comment, CommentDTO>{

    private ModelMapper modelMapper;


    private UserService userService;

    @Override
    public Comment toEntity(CommentDTO dto) {
//        Comment entity = modelMapper.map(dto, Comment.class);
        Comment entity = new Comment();
        entity.setStatus(dto.getStatus());
        entity.setSender(userService.findByEmail(dto.getSender().getEmail()));
//        entity.setSenderName(dto.getSender().getName());
//        entity.setSender(userDtoMapper.toEntity(dto.getSender()));
        entity.setText(dto.getText());
//        entity.setRentInfo(rentInfoDtoMapper.toEntity(dto.getRentInfo()));
        return entity;
    }

    @Override
    public CommentDTO toDto(Comment entity) {
//        CommentDTO dto = modelMapper.map(entity, CommentDTO.class);
        CommentDTO dto = new CommentDTO();
        UserDTO sender = new UserDTO();
        sender.setName(entity.getSender().getName());
        sender.setId(entity.getSender().getId());
        dto.setSender(sender);
        dto.setStatus(entity.getStatus());
        dto.setText(entity.getText());
        dto.setId(entity.getId());
        return dto;
    }

    @Autowired
    public CommentDtoMapper(ModelMapper modelMapper, UserService userService) {
        this.modelMapper = modelMapper;
        this.userService = userService;
    }
}
