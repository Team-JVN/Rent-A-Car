package jvn.Users.service;

import jvn.Users.model.Agent;
import jvn.Users.model.Role;
import jvn.Users.model.User;
import jvn.Users.model.UserTokenState;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;

public interface UserService {

    User findByEmail(String email);

    Role findRoleByName(String name);

    Agent getLoginAgent();

    User getLoginUser();

    void generateResetToken(String email) throws NoSuchAlgorithmException;

    UserTokenState refreshAuthenticationToken(HttpServletRequest request);

    User verify();
}

