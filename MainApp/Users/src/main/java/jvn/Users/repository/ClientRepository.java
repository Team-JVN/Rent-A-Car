package jvn.Users.repository;

import jvn.Users.enumeration.ClientStatus;
import jvn.Users.model.Client;
import jvn.Users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Client findByPhoneNumber(String phoneNumber);

    Client findByPhoneNumberAndIdNot(String phoneNumber, Long id);

    List<Client> findAll();

    List<Client> findByStatusNot(ClientStatus clientStatus);

    List<Client> findByStatus(ClientStatus clientStatus);

    Client findOneById(Long id);
}
