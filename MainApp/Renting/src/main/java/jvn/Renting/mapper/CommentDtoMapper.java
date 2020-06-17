package jvn.Renting.mapper;

import jvn.Renting.dto.both.CommentDTO;
import jvn.Renting.dto.both.RentInfoDTO;
import jvn.Renting.dto.both.UserDTO;
import jvn.Renting.model.Comment;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentDtoMapper implements MapperInterface<Comment, CommentDTO>{

    private ModelMapper modelMapper;


    @Override
    public Comment toEntity(CommentDTO dto) {
//        Comment entity = modelMapper.map(dto, Comment.class);
        Comment entity = new Comment();
        entity.setStatus(dto.getStatus());
        entity.setSenderId(dto.getSender().getId());
        entity.setSenderName(dto.getSender().getName());
        entity.setText(dto.getText());
//        entity.setRentInfo(rentInfoDtoMapper.toEntity(dto.getRentInfo()));
        return entity;
    }

    @Override
    public CommentDTO toDto(Comment entity) {
//        CommentDTO dto = modelMapper.map(entity, CommentDTO.class);
        CommentDTO dto = new CommentDTO();
        UserDTO sender = new UserDTO();
        sender.setName(entity.getSenderName());
        sender.setId(entity.getSenderId());
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
