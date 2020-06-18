package jvn.RentACar.mapper;

import jvn.RentACar.dto.soap.rentrequest.RentRequestDetails;
import jvn.RentACar.enumeration.RentRequestStatus;
import jvn.RentACar.model.RentRequest;
import jvn.RentACar.service.ClientService;
import jvn.RentACar.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class RentRequestDetailsMapper implements MapperInterface<RentRequest, RentRequestDetails> {
    private ModelMapper modelMapper;

    private RentInfoDetailsMapper rentInfoDetailsMapper;

    private ClientService clientService;

    private UserService userService;

    @Override
    public RentRequest toEntity(RentRequestDetails dto) {
        RentRequest entity = new RentRequest();
        entity.setId(null);
        entity.setMainAppId(dto.getId());
        entity.setRentRequestStatus(getStatus(dto.getStatus()));
        entity.setClient(clientService.getByMainAppId(dto.getClient()));
        entity.setTotalPrice(dto.getTotalPrice());
        entity.setRentInfos(dto.getRentInfo().stream().map(rentInfoDetailsMapper::toEntity).collect(Collectors.toSet()));
//        entity.setMessages(dto.getMessage().stream().map(messageDetailsMapper::toEntity).collect(Collectors.toSet()));
        entity.setCreatedBy(userService.getByMainAppId(dto.getCreatedBy()));
        return entity;
    }

    @Override
    public RentRequestDetails toDto(RentRequest entity) {
        RentRequestDetails dto = new RentRequestDetails();
        dto.setId(entity.getMainAppId());
        dto.setStatus(entity.getRentRequestStatus().toString());
        dto.setClient(entity.getClient().getMainAppId());
        dto.setTotalPrice(entity.getTotalPrice());
        dto.getRentInfo().addAll(entity.getRentInfos().stream().map(rentInfoDetailsMapper::toDto).collect(Collectors.toList()));
//        dto.getMessage().addAll(entity.getMessages().stream().map(messageDetailsMapper::toDto).collect(Collectors.toList()));
        dto.setCreatedBy(entity.getCreatedBy().getMainAppId());
        dto.setAdvertisementOwner(entity.getRentInfos().iterator().next().getAdvertisement().getCar().getOwner().getMainAppId());
        return dto;
    }

    private RentRequestStatus getStatus(String status) {
        try {
            return RentRequestStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

    @Autowired
    public RentRequestDetailsMapper(ModelMapper modelMapper, RentInfoDetailsMapper rentInfoDetailsMapper,
                                    ClientService clientService, UserService userService) {
        this.modelMapper = modelMapper;
        this.rentInfoDetailsMapper = rentInfoDetailsMapper;
        this.clientService = clientService;
        this.userService = userService;
    }
}