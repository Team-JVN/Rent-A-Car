package jvn.RentACar.mapper;

import jvn.RentACar.dto.both.ClientDTO;
import jvn.RentACar.model.Client;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientDtoMapper implements MapperInterface<Client, ClientDTO> {
    public ModelMapper modelMapper;

    @Override
    public Client toEntity(ClientDTO dto) {
        Client client = modelMapper.map(dto, Client.class);
        return client;
    }

    @Override
    public ClientDTO toDto(Client entity) {
        ClientDTO carDTO = modelMapper.map(entity, ClientDTO.class);
        return carDTO;
    }

    @Autowired
    public ClientDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
