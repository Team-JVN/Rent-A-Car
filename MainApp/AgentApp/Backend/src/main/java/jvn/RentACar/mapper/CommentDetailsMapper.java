package jvn.RentACar.mapper;

import jvn.RentACar.dto.soap.comment.CommentDetails;
import jvn.RentACar.dto.soap.rentrequest.RentRequestDetails;
import jvn.RentACar.enumeration.CommentStatus;
import jvn.RentACar.model.Comment;
import jvn.RentACar.model.RentRequest;
import jvn.RentACar.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.stream.Collectors;

@Component
public class CommentDetailsMapper implements MapperInterface<Comment, CommentDetails> {

    private ModelMapper modelMapper;

    private UserService userService;

    private CommentStatus getStatus(String status) {
        try {
            return CommentStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Comment toEntity(CommentDetails dto) throws ParseException {
        Comment entity = new Comment();
        entity.setId(null);
        entity.setMainAppId(dto.getId());
        entity.setStatus(getStatus(dto.getStatus()));
        entity.setSender(userService.getByMainAppId(dto.getUserId()));
        entity.setText(dto.getText());
        return entity;
    }

    @Override
    public CommentDetails toDto(Comment entity) {
        CommentDetails dto = new CommentDetails();
        dto.setId(entity.getMainAppId());
        dto.setStatus(entity.getStatus().toString());
        dto.setUserId(entity.getSender().getMainAppId());
        dto.setUserName(entity.getSender().getName());
        dto.setText(entity.getText());
        return dto;
    }

    @Autowired
    public CommentDetailsMapper(ModelMapper modelMapper, UserService userService) {
        this.modelMapper = modelMapper;
        this.userService = userService;
    }
}
