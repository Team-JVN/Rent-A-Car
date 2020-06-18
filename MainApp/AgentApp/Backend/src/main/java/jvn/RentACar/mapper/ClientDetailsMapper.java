package jvn.RentACar.mapper;

import jvn.RentACar.dto.soap.client.ClientDetails;
import jvn.RentACar.enumeration.ClientStatus;
import jvn.RentACar.enumeration.LogicalStatus;
import jvn.RentACar.model.Client;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientDetailsMapper implements MapperInterface<Client, ClientDetails> {

    public ModelMapper modelMapper;

    @Override
    public Client toEntity(ClientDetails dto) {
        Client entity = modelMapper.map(dto, Client.class);
        entity.setMainAppId(dto.getId());
        entity.setStatus(getStatus(dto.getStatus()));
        return entity;
    }

    @Override
    public ClientDetails toDto(Client entity) {
        ClientDetails dto = modelMapper.map(entity, ClientDetails.class);
        dto.setId(entity.getMainAppId());
        return dto;
    }

    private ClientStatus getStatus(String status) {
        try {
            return ClientStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

    @Autowired
    public ClientDetailsMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}

