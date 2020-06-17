package jvn.RentACar.mapper;

import jvn.RentACar.dto.both.CommentDTO;
import jvn.RentACar.dto.both.UserDTO;
import jvn.RentACar.model.Comment;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentDtoMapper implements MapperInterface<Comment, CommentDTO>{

    private ModelMapper modelMapper;

    private UserDtoMapper userDtoMapper;


    @Override
    public Comment toEntity(CommentDTO dto) {
//        Comment entity = modelMapper.map(dto, Comment.class);
        Comment entity = new Comment();
        entity.setStatus(dto.getStatus());
        entity.setSender(userDtoMapper.toEntity(dto.getSender()));
//        entity.setSenderName(dto.getSender().getName());
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
    public CommentDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
