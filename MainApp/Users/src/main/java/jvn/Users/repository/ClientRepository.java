package jvn.Users.repository;

import jvn.Users.enumeration.ClientStatus;
import jvn.Users.model.Client;
import jvn.Users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Client findByPhoneNumber(String phoneNumber);

    Client findByPhoneNumberAndIdNot(String phoneNumber, Long id);

    List<Client> findAll();

    List<Client> findByStatusIn(Collection<ClientStatus> statuses);

    List<Client> findByStatusNotAndIdNot(ClientStatus clientStatus, Long id);

    List<Client> findByStatusAndIdNot(ClientStatus clientStatus, Long id);

    Client findOneByIdAndStatusNot(Long id, ClientStatus clientStatus);

    Client findOneByIdAndStatus(Long id, ClientStatus clientStatus);

    Client findOneByIdAndStatusIn(Long id, Collection<ClientStatus> clientStatus);
}
