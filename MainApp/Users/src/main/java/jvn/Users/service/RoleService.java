package jvn.Users.service;

import jvn.Users.dto.both.RoleDTO;
import jvn.Users.model.Role;

import java.util.List;

public interface RoleService {

    Role editPermissions(Long id, RoleDTO role);

    List<Role> getAll();

    Role get(Long id);

}
