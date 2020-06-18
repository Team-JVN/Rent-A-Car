package jvn.Users.mapper;

import jvn.Users.dto.soap.client.ClientDetails;
import jvn.Users.model.Client;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientDetailsMapper implements MapperInterface<Client, ClientDetails> {

    public ModelMapper modelMapper;

    @Override
    public Client toEntity(ClientDetails dto) {
        Client entity = modelMapper.map(dto, Client.class);
        return entity;
    }

    @Override
    public ClientDetails toDto(Client entity) {
        ClientDetails dto = modelMapper.map(entity, ClientDetails.class);
        return dto;
    }

    @Autowired
    public ClientDetailsMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}

