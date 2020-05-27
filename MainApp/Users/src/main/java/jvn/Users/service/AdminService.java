package jvn.Users.service;

import jvn.Users.model.Admin;
import jvn.Users.model.Agent;

import java.util.BitSet;
import java.util.List;

public interface AdminService {

    Admin create(Admin admin);

    Admin get(Long id);

    List<Admin> getAll(String status);

    void delete(Long id);

    Admin edit(Long id, Admin admin);
}
