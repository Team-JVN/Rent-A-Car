package jvn.RentACar.service;

import jvn.RentACar.dto.both.RoleDTO;
import jvn.RentACar.model.Role;

import java.util.List;

public interface RoleService {

    Role editPermissions(Long id, RoleDTO role);

    List<Role> getAll();

    Role get(Long id);

}
