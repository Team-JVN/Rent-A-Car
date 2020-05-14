package jvn.RentACar.repository;

import jvn.RentACar.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);

    Role findOneById(Long id);

}
