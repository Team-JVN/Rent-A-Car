package jvn.Users.service;

import jvn.Users.model.Permission;

import java.util.List;

public interface PermissionService {

    List<Permission> getAll();

    Permission get(Long id);
}
