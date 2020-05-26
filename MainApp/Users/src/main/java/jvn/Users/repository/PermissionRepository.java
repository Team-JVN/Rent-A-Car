package jvn.Users.repository;

import jvn.Users.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Permission findByName(String name);

    Permission findOneById(Long id);

    List<Permission> findByNameNot(String name);
}

