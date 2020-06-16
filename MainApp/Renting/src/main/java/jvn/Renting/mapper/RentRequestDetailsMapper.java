package jvn.Renting.mapper;

import jvn.Renting.dto.soap.rentrequest.RentRequestDetails;
import jvn.Renting.enumeration.RentRequestStatus;
import jvn.Renting.model.RentRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class RentRequestDetailsMapper implements MapperInterface<RentRequest, RentRequestDetails> {
    private ModelMapper modelMapper;

    private RentInfoDetailsMapper rentInfoDetailsMapper;

    @Override
    public RentRequest toEntity(RentRequestDetails dto) {
        RentRequest entity = new RentRequest();
        entity.setId(dto.getId());
        entity.setRentRequestStatus(RentRequestStatus.valueOf(dto.getRentRequestStatus()));
        entity.setClient(dto.getClient());
        entity.setTotalPrice(dto.getTotalPrice());
        entity.setRentInfos(dto.getRentInfo().stream().map(rentInfoDetailsMapper::toEntity).collect(Collectors.toSet()));
//        entity.setMessages(dto.getMessage().stream().map(messageDetailsMapper::toEntity).collect(Collectors.toSet()));
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setAdvertisementOwner(dto.getAdvertisementOwner());
        return entity;
    }

    @Override
    public RentRequestDetails toDto(RentRequest entity) {
        RentRequestDetails dto = new RentRequestDetails();
        dto.setId(entity.getId());
        dto.setRentRequestStatus(entity.getRentRequestStatus().toString());
        dto.setClient(entity.getClient());
        dto.setTotalPrice(entity.getTotalPrice());
        dto.getRentInfo().addAll(entity.getRentInfos().stream().map(rentInfoDetailsMapper::toDto).collect(Collectors.toList()));
//        dto.getMessage().addAll(entity.getMessages().stream().map(messageDetailsMapper::toDto).collect(Collectors.toList()));
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setAdvertisementOwner(entity.getAdvertisementOwner());
        return dto;
    }

    @Autowired
    public RentRequestDetailsMapper(ModelMapper modelMapper, RentInfoDetailsMapper rentInfoDetailsMapper) {
        this.modelMapper = modelMapper;
        this.rentInfoDetailsMapper = rentInfoDetailsMapper;
    }
}