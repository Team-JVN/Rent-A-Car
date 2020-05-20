package jvn.RentACar.service;

import jvn.RentACar.model.Agent;
import jvn.RentACar.model.Role;
import jvn.RentACar.model.User;
import jvn.RentACar.model.UserTokenState;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;

public interface UserService {

    User findByEmail(String email);

    Role findRoleByName(String name);

    Agent getLoginAgent();

    User getLoginUser();

    void generateResetToken(String email) throws NoSuchAlgorithmException;

    UserTokenState refreshAuthenticationToken(HttpServletRequest request);
}
