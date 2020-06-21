package jvn.Renting.mapper;

import jvn.Renting.dto.both.CommentDTO;
import jvn.Renting.dto.both.MessageDTO;
import jvn.Renting.dto.both.RentInfoDTO;
import jvn.Renting.dto.both.RentRequestDTO;
import jvn.Renting.model.Comment;
import jvn.Renting.model.Message;
import jvn.Renting.model.RentInfo;
import jvn.Renting.model.RentRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class RentRequestDtoMapper implements MapperInterface<RentRequest, RentRequestDTO> {
    private ModelMapper modelMapper;

    private CommentDtoMapper commentDtoMapper;

    private RentInfoDtoMapper rentInfoDtoMapper;

    @Override
    public RentRequest toEntity(RentRequestDTO dto) {
        RentRequest entity = new RentRequest();
        if (dto.getClient() != null) {
            entity.setClient(dto.getClient().getId());
        }
        entity.setTotalPrice(dto.getTotalPrice());
        entity.setId(dto.getId());
        List<RentInfo> entityRentInfos = new ArrayList<>(dto.getRentInfos().size());
        for (RentInfoDTO rentInfoDTO : dto.getRentInfos()) {

            //TODO: add mapping comments
//            Set<Comment> comments = new HashSet<>();
//            if(!rentInfoDTO.getComments().isEmpty() && rentInfoDTO.getComments() != null){
//                for(CommentDTO commentDTO: rentInfoDTO.getComments()){
//
//                    Comment comment = commentDtoMapper.toEntity(commentDTO);
//                    comments.add(comment);
//                }
//            }
            RentInfo rentInfo = new RentInfo();
            rentInfo.setDateTimeFrom(getLocalDateTime(rentInfoDTO.getDateTimeFrom()));
            rentInfo.setDateTimeTo(getLocalDateTime(rentInfoDTO.getDateTimeTo()));
            rentInfo.setAdvertisement(rentInfoDTO.getAdvertisement().getId());
            rentInfo.setOptedForCDW(rentInfoDTO.getOptedForCDW());
            rentInfo.setId(rentInfoDTO.getId());
//            rentInfo.setComments(comments);
            Set<Comment> comments = new HashSet<Comment>();
            if (rentInfoDTO.getComments() != null) {
                for (CommentDTO commentDTO : rentInfoDTO.getComments()) {
                    comments.add(commentDtoMapper.toEntity(commentDTO));
                }
                rentInfo.setComments(comments);
            }
            entityRentInfos.add(rentInfo);
        }
        entity.setRentInfos(new HashSet<>(entityRentInfos));
        return entity;
    }

    @Override
    public RentRequestDTO toDto(RentRequest entity) {
        RentRequestDTO dto = modelMapper.map(entity, RentRequestDTO.class);
        Set<RentInfoDTO> rentInfos = new HashSet<RentInfoDTO>();

        for (RentInfo rentInfo : entity.getRentInfos()) {

            rentInfos.add(rentInfoDtoMapper.toDto(rentInfo));

        }
        dto.setRentInfos(rentInfos);
        return dto;
    }

    private LocalDateTime getLocalDateTime(String date) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date.substring(0, 10), formatter);
        LocalTime localTime = LocalTime.parse(date.substring(11), DateTimeFormatter.ofPattern("HH:mm"));
        return LocalDateTime.of(localDate, localTime);
    }

    @Autowired
    public RentRequestDtoMapper(ModelMapper modelMapper, CommentDtoMapper commentDtoMapper, RentInfoDtoMapper rentInfoDtoMapper) {
        this.modelMapper = modelMapper;
        this.commentDtoMapper = commentDtoMapper;
        this.rentInfoDtoMapper = rentInfoDtoMapper;
    }
}