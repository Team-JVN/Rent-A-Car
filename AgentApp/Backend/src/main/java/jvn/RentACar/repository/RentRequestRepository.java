package jvn.RentACar.repository;

import jvn.RentACar.enumeration.RentRequestStatus;
import jvn.RentACar.model.RentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentRequestRepository extends JpaRepository<RentRequest, Long> {

    List<RentRequest> findAll();

    RentRequest findOneById(Long id);

    List<RentRequest> findByRentRequestStatus(RentRequestStatus rentRequestStatus);

}
