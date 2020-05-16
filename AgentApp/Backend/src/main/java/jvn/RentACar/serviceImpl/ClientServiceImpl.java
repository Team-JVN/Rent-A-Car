package jvn.RentACar.serviceImpl;

import jvn.RentACar.common.RandomPasswordGenerator;
import jvn.RentACar.exceptionHandler.InvalidClientDataException;
import jvn.RentACar.model.Client;
import jvn.RentACar.model.Role;
import jvn.RentACar.repository.ClientRepository;
import jvn.RentACar.service.ClientService;
import jvn.RentACar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {

    private ClientRepository clientRepository;

    private UserService userService;

    private PasswordEncoder passwordEncoder;

    @Override
    public Client create(Client client) {
        //TODO:Posaljes mejl klijentu.
        if (clientRepository.findByPhoneNumber(client.getPhoneNumber()) != null) {
            throw new InvalidClientDataException("Client with same phone number already exists.", HttpStatus.BAD_REQUEST);
        }
        if (userService.findByEmail(client.getEmail()) != null) {
            throw new InvalidClientDataException("Client with same email address already exists.", HttpStatus.BAD_REQUEST);
        }
        if (client.getPassword() == null) {
            RandomPasswordGenerator randomPasswordGenerator = new RandomPasswordGenerator();
            String generatedPassword = randomPasswordGenerator.generatePassword();
            client.setPassword(generatedPassword);
        }
        client.setEnabled(true);
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        Role role = userService.findRoleByName("ROLE_CLIENT");
        client.setRole(role);

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
        if (!dbClient.getClientRentRequests().isEmpty()) {
            throw new InvalidClientDataException("This client has at least one request so you can not delete this client.", HttpStatus.BAD_REQUEST);
        }
        dbClient.setRole(null);
        clientRepository.deleteById(id);
    }

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository, UserService userService, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
}
