package jvn.Users.repository;

import jvn.Users.enumeration.AdminStatus;
import jvn.Users.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Admin findOneById(Long id);
    List<Admin> findAllByStatus(AdminStatus adminStatus);
}
