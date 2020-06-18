package jvn.RentACar.service;

import jvn.RentACar.model.*;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;

public interface UserService {

    User findByEmail(String email);

    Role findRoleByName(String name);

    Agent getLoginAgent();

    User getLoginUser();

    Client getLoginClient();

    void generateResetToken(String email) throws NoSuchAlgorithmException;

    UserTokenState refreshAuthenticationToken(HttpServletRequest request);

    User getByMainAppId(Long mainAppId);
}
