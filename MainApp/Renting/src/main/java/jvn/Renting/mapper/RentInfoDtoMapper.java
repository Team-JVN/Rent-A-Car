package jvn.Renting.mapper;

import jvn.Renting.dto.both.CommentDTO;
import jvn.Renting.dto.both.RentInfoDTO;
import jvn.Renting.model.Comment;
import jvn.Renting.model.RentInfo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Set;

@Component
public class RentInfoDtoMapper implements MapperInterface<RentInfo, RentInfoDTO> {
    private ModelMapper modelMapper;

    private CommentDtoMapper commentDtoMapper;

    @Override
    public RentInfo toEntity(RentInfoDTO dto) {
        RentInfo rentInfo = new RentInfo();
        rentInfo.setDateTimeFrom(getLocalDateTime(dto.getDateTimeFrom()));
        rentInfo.setDateTimeTo(getLocalDateTime(dto.getDateTimeTo()));
        rentInfo.setAdvertisement(dto.getAdvertisement().getId());
        rentInfo.setOptedForCDW(dto.getOptedForCDW());
        rentInfo.setId(dto.getId());
        Set<Comment> comments = new HashSet<Comment>();
        if(dto.getComments() != null){
            for(CommentDTO commentDTO: dto.getComments()){
                comments.add(commentDtoMapper.toEntity(commentDTO));
            }
        }
        rentInfo.setComments(comments);
        rentInfo.setRating(dto.getRating());
        return rentInfo;
    }

    @Override
    public RentInfoDTO toDto(RentInfo entity) {
        RentInfoDTO dto = modelMapper.map(entity, RentInfoDTO.class);
        return dto;
    }

    private LocalDateTime getLocalDateTime(String date) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date.substring(0, 10), formatter);
        LocalTime localTime = LocalTime.parse(date.substring(11), DateTimeFormatter.ofPattern("HH:mm"));
        return LocalDateTime.of(localDate, localTime);
    }

    @Autowired
    public RentInfoDtoMapper(ModelMapper modelMapper, CommentDtoMapper commentDtoMapper) {
        this.modelMapper = modelMapper;
        this.commentDtoMapper = commentDtoMapper;
    }
}
