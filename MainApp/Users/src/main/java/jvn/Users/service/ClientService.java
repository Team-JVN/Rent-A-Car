package jvn.Users.service;

import jvn.Users.model.Client;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface ClientService {

    Client create(Client client) throws NoSuchAlgorithmException;

    Client get(Long id);
//
//    List<Client> get();
//
//    Client edit(Long id, Client client);
//
//    void delete(Long id);
//
    Client activateAccount(String token) throws NoSuchAlgorithmException;
}
