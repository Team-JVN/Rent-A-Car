package jvn.Renting.mapper;

import jvn.Renting.dto.soap.comment.CommentDetails;
import jvn.Renting.enumeration.CommentStatus;
import jvn.Renting.model.Comment;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
public class CommentDetailsMapper implements MapperInterface<Comment, CommentDetails> {

    private ModelMapper modelMapper;


    private CommentStatus getStatus(String status) {
        try {
            return CommentStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Comment toEntity(CommentDetails dto){
        Comment entity = new Comment();
        entity.setId(dto.getId());
        entity.setStatus(getStatus(dto.getStatus()));
        entity.setSenderName(dto.getUserName());
        entity.setSenderId(dto.getUserId());
        entity.setText(dto.getText());
        return entity;
    }

    @Override
    public CommentDetails toDto(Comment entity) {
        CommentDetails dto = new CommentDetails();
        dto.setId(entity.getId());
        dto.setStatus(entity.getStatus().toString());
        dto.setUserId(entity.getSenderId());
        dto.setUserName(entity.getSenderName());
        dto.setText(entity.getText());
        return dto;
    }

    @Autowired
    public CommentDetailsMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
