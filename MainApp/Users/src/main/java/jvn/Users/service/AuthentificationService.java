package jvn.Users.service;

import jvn.Users.dto.request.ChangePasswordDTO;
import jvn.Users.dto.request.ResetPasswordDTO;
import jvn.Users.model.UserTokenState;
import jvn.Users.security.JwtAuthenticationRequest;

import java.security.NoSuchAlgorithmException;

public interface AuthentificationService {

    UserTokenState login(JwtAuthenticationRequest authenticationRequest);

    void changePassword(ChangePasswordDTO changePasswordDTO) throws NullPointerException, NoSuchAlgorithmException;

    void checkPassword(String password) throws NoSuchAlgorithmException;

    void resetPassword(String token, ResetPasswordDTO resetPasswordDTO) throws NoSuchAlgorithmException;

    boolean userIsNeverLoggedIn(String email);
}
