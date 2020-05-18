package jvn.RentACar.service;

import jvn.RentACar.model.Permission;

import java.util.List;

public interface PermissionService {

    List<Permission> getAll();

    Permission get(Long id);
}
