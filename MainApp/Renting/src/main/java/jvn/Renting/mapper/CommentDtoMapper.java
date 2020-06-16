package jvn.Renting.mapper;

import jvn.Renting.dto.both.CommentDTO;
import jvn.Renting.dto.both.RentInfoDTO;
import jvn.Renting.model.Comment;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentDtoMapper implements MapperInterface<Comment, CommentDTO>{

    private ModelMapper modelMapper;

    private RentInfoDtoMapper rentInfoDtoMapper;

    @Override
    public Comment toEntity(CommentDTO dto) {
//        Comment entity = modelMapper.map(dto, Comment.class);
        Comment entity = new Comment();
        entity.setStatus(dto.getStatus());
        entity.setSender(dto.getSender().getId());
        entity.setText(dto.getText());
//        entity.setRentInfo(rentInfoDtoMapper.toEntity(dto.getRentInfo()));
        return entity;
    }

    @Override
    public CommentDTO toDto(Comment entity) {
        System.out.println("APPROVAL to dto");
        CommentDTO dto = modelMapper.map(entity, CommentDTO.class);
        System.out.println("APPROVAL to dto2");
        return dto;
    }

    @Autowired
    public CommentDtoMapper(ModelMapper modelMapper, RentInfoDtoMapper rentInfoDtoMapper) {
        this.modelMapper = modelMapper;
        this.rentInfoDtoMapper = rentInfoDtoMapper;
    }
}
