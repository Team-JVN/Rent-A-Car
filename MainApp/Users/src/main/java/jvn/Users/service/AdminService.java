package jvn.Users.service;

import jvn.Users.model.Admin;
import jvn.Users.model.Agent;

import java.util.BitSet;
import java.util.List;

public interface AdminService {

    Admin create(Admin admin);

    Admin get(Long id);

    List<Admin> getAll(String status,Long id);

    void delete(Long id);

    Admin edit(Long loggedInUserId,Admin admin);
}
