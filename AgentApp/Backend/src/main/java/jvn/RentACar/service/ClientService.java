package jvn.RentACar.service;

import jvn.RentACar.model.Client;

import java.util.List;

public interface ClientService {

    Client create(Client client);

    Client get(Long id);

    List<Client> get();

    Client edit(Long id, Client client);

    void delete(Long id);

    Client activateAccount(Long id);
}
