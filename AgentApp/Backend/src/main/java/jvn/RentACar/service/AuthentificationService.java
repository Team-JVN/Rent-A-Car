package jvn.RentACar.service;

import jvn.RentACar.dto.request.ChangePasswordDTO;
import jvn.RentACar.dto.request.ResetPasswordDTO;
import jvn.RentACar.model.UserTokenState;
import jvn.RentACar.security.JwtAuthenticationRequest;

import java.security.NoSuchAlgorithmException;

public interface AuthentificationService {

    UserTokenState login(JwtAuthenticationRequest authenticationRequest);

    void changePassword(ChangePasswordDTO changePasswordDTO) throws NullPointerException, NoSuchAlgorithmException;

    void checkPassword(String password) throws NoSuchAlgorithmException;

    void resetPassword(String token, ResetPasswordDTO resetPasswordDTO) throws NoSuchAlgorithmException;

}
