package jvn.Renting.mapper;

import jvn.Renting.dto.both.CommentDTO;
import jvn.Renting.model.Comment;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentDtoMapper implements MapperInterface<Comment, CommentDTO>{

    private ModelMapper modelMapper;

    @Override
    public Comment toEntity(CommentDTO dto) {
        Comment entity = modelMapper.map(dto, Comment.class);
        return entity;
    }

    @Override
    public CommentDTO toDto(Comment entity) {
        CommentDTO dto = modelMapper.map(entity, CommentDTO.class);
        return dto;
    }

    @Autowired
    public CommentDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
