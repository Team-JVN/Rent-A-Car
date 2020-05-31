package jvn.Users.repository;

import jvn.Users.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);

    Role findOneById(Long id);

    List<Role> findByNameNot(String name);
}
