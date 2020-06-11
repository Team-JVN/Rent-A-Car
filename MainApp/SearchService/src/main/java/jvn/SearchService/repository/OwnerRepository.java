package jvn.SearchService.repository;

import jvn.SearchService.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {

    Owner findOneById(Long id);
}
