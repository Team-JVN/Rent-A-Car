package jvn.RentACar.service;

import jvn.RentACar.dto.request.ChangePasswordDTO;
import jvn.RentACar.model.UserTokenState;
import jvn.RentACar.security.JwtAuthenticationRequest;

public interface AuthentificationService {

    UserTokenState login(JwtAuthenticationRequest authenticationRequest);

    void changePassword(ChangePasswordDTO changePasswordDTO) throws NullPointerException;
}
