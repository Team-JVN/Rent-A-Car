package jvn.RentACar.service;

import jvn.RentACar.dto.request.ChangePasswordDTO;
import jvn.RentACar.dto.response.LoggedInUserDTO;
import jvn.RentACar.model.Agent;
import jvn.RentACar.model.Role;
import jvn.RentACar.model.User;
import jvn.RentACar.security.JwtAuthenticationRequest;

public interface UserService {

    User findByEmail(String email);

    Role findRoleByName(String name);

    LoggedInUserDTO login(JwtAuthenticationRequest authenticationRequest);

    void changePassword(ChangePasswordDTO changePasswordDTO) throws NullPointerException;

    Agent getLoginAgent();

    User getLoginUser();
}
