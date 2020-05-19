package jvn.RentACar.listener;

import jvn.RentACar.serviceImpl.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureListener
        implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Autowired
    private LoginAttemptService loginAttemptService;

    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {
        loginAttemptService.loginFailed();
    }
}