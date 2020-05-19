package jvn.RentACar.service;

import jvn.RentACar.dto.request.ChangePasswordDTO;
import jvn.RentACar.dto.request.ResetPasswordDTO;
import jvn.RentACar.model.UserTokenState;
import jvn.RentACar.security.JwtAuthenticationRequest;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public interface AuthentificationService {

    UserTokenState login(JwtAuthenticationRequest authenticationRequest);

    void changePassword(ChangePasswordDTO changePasswordDTO) throws NullPointerException, UnsupportedEncodingException, NoSuchAlgorithmException;

    void checkPassword(String password) throws UnsupportedEncodingException, NoSuchAlgorithmException;

    void resetPassword(String token, ResetPasswordDTO resetPasswordDTO) throws UnsupportedEncodingException, NoSuchAlgorithmException;

}
