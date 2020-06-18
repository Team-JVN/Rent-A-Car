package jvn.Users.service;

import jvn.Users.dto.response.UserDTO;
import jvn.Users.enumeration.ClientStatus;
import jvn.Users.model.Client;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface ClientService {

    Client create(Client client, boolean fromAgentApp, Long user) throws NoSuchAlgorithmException;

    Client get(Long id, ClientStatus status);

    boolean verify(Long id);

    List<Client> get(String status, Long id);

    List<Client> getForRentRequest(Long loggedInClientId);

    Client edit(Long id, Client client);

    void delete(Long id);

    Client activateAccount(String token) throws NoSuchAlgorithmException;

    Client approveRequestToRegister(Long id) throws NoSuchAlgorithmException;

    Client rejectRequestToRegister(Long id, String reason);

    Client block(Long id);

    Client unblock(Long id);

    Client createRentRequests(Long id, String status);

    Client createComments(Long id, String status);

    List<Client> getClientsById(List<Long> clients);

    String checkClientPersonalInfo(String email, String phoneNumber);

    boolean checkIfCanDeleteAndDelete(Long id, Long loggedInUser);

    List<Client> getAll(Long id);
}
