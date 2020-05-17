package jvn.RentACar.service;

import jvn.RentACar.dto.request.ChangePasswordDTO;
import jvn.RentACar.model.Agent;
import jvn.RentACar.model.Role;
import jvn.RentACar.model.User;
import jvn.RentACar.model.UserTokenState;
import jvn.RentACar.security.JwtAuthenticationRequest;

import javax.servlet.http.HttpServletRequest;

public interface UserService {

    User findByEmail(String email);

    Role findRoleByName(String name);

    Agent getLoginAgent();

    User getLoginUser();

    UserTokenState refreshAuthenticationToken(HttpServletRequest request);
}
