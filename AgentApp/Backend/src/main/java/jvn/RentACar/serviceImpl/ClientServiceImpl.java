package jvn.RentACar.serviceImpl;

import jvn.RentACar.exceptionHandler.InvalidClientDataException;
import jvn.RentACar.model.Client;
import jvn.RentACar.repository.ClientRepository;
import jvn.RentACar.repository.UserRepository;
import jvn.RentACar.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {

    private ClientRepository clientRepository;

    private UserRepository userRepository;

    @Override
    public Client create(Client client) {
        //TODO: Kada agent kreira klijenta tom klijentu izgenerises sifru i posaljes mu mejl.
        if (clientRepository.findByPhoneNumber(client.getPhoneNumber()) != null) {
            throw new InvalidClientDataException("Client with same phone number already exists.", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.findByEmail(client.getEmail()) != null) {
            throw new InvalidClientDataException("Client with same email address already exists.", HttpStatus.BAD_REQUEST);
        }
        return clientRepository.save(client);
    }

    @Override
    public Client get(Long id) {
        Client client = clientRepository.findOneById(id);
        if (client == null) {
            throw new InvalidClientDataException("This client doesn't exist.", HttpStatus.NOT_FOUND);
        }
        return client;
    }

    @Override
    public List<Client> get() {
        return clientRepository.findAll();
    }

    @Override
    public Client edit(Long id, Client client) {
        Client dbClient = get(client.getId());
        if (clientRepository.findByPhoneNumberAndIdNot(client.getPhoneNumber(), id) != null) {
            throw new InvalidClientDataException("Client with same phone number already exists.", HttpStatus.BAD_REQUEST);
        }
        dbClient.setPhoneNumber(client.getPhoneNumber());
        dbClient.setName(client.getName());
        dbClient.setAddress(client.getAddress());
        return clientRepository.save(dbClient);
    }

    @Override
    public void delete(Long id) {
        Client dbClient = get(id);
        if (!dbClient.getRentRequests().isEmpty()) {
            throw new InvalidClientDataException("This client has at least one request so you can not delete this client.", HttpStatus.FORBIDDEN);
        }
        clientRepository.deleteById(id);
    }

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository, UserRepository userRepository) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
    }
}
