package jvn.RentACar.repository;

import jvn.RentACar.enumeration.ClientStatus;
import jvn.RentACar.model.Client;
import jvn.RentACar.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Client findByPhoneNumber(String phoneNumber);

    Client findByPhoneNumberAndIdNot(String phoneNumber, Long id);

    List<Client> findAll();

    Client findOneById(Long id);

    List<Client> findAllByStatusNot(ClientStatus status);
}
