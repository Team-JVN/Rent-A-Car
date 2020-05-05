package jvn.RentACar.service;

import jvn.RentACar.dto.request.ChangePasswordDTO;
import jvn.RentACar.dto.response.LoggedInUserDTO;
import jvn.RentACar.model.Agent;
import jvn.RentACar.model.Authority;
import jvn.RentACar.model.User;
import jvn.RentACar.security.JwtAuthenticationRequest;

import java.util.Set;

public interface UserService {

    User findByEmail(String email);

    Set<Authority> findByName(String name);

    LoggedInUserDTO login(JwtAuthenticationRequest authenticationRequest);

    void changePassword(ChangePasswordDTO changePasswordDTO) throws NullPointerException;

    Agent getLoginAgent();

    User getLoginUser();
}
