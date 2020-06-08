package jvn.RentACar.serviceImpl;

import jvn.RentACar.exceptionHandler.BlockedUserException;
import jvn.RentACar.enumeration.AgentStatus;
import jvn.RentACar.enumeration.ClientStatus;
import jvn.RentACar.exceptionHandler.InvalidUserDataException;
import jvn.RentACar.model.*;
import jvn.RentACar.repository.ResetTokenRepository;
import jvn.RentACar.repository.RoleRepository;
import jvn.RentACar.repository.UserRepository;
import jvn.RentACar.security.TokenUtils;
import jvn.RentACar.service.EmailNotificationService;
import jvn.RentACar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {

    private Environment environment;

    private UserRepository userRepository;

    public TokenUtils tokenUtils;

    private RoleRepository roleRepository;

    private LoginAttemptService loginAttemptService;

    private HttpServletRequest request;

    private ResetTokenRepository resetTokenRepository;

    private EmailNotificationService emailNotificationService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // recommended work rounds is 12 (default is 10)
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Role findRoleByName(String name) {
        return this.roleRepository.findByName(name);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String ip = getClientIP();
        if (loginAttemptService.isBlocked(ip)) {
            throw new RuntimeException("Blocked");
        }
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with email '%s'.", username));
        } else {
            return user;
        }
    }

    @Override
    public Agent getLoginAgent() {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();

        return (Agent) userRepository.findByEmail(currentUser.getName());
    }

    @Override
    public User getLoginUser() {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(currentUser.getName());
    }

    @Override
    public Client getLoginClient() {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        return (Client) userRepository.findByEmail(currentUser.getName());
    }

    @Override
    public void generateResetToken(String email) throws NoSuchAlgorithmException {
        User user = findByEmail(email);
        if (user == null) {
            return;
        }

        if (user instanceof Client && ((Client) user).getStatus() != ClientStatus.ACTIVE) {
            composeAndSendEmail(user.getEmail());
            return;
        } else if (user instanceof Agent && ((Agent) user).getStatus() != AgentStatus.ACTIVE) {
            composeAndSendEmail(user.getEmail());
            return;
        }

        ResetToken resetToken = new ResetToken(user);
        String nonHashedToken = resetToken.getToken();
        resetToken.setToken(getTokenHash(nonHashedToken));

        ResetToken dbToken = resetTokenRepository.findByToken(resetToken.getToken());
        while (dbToken != null) {
            resetToken = new ResetToken(user);
            dbToken = resetTokenRepository.findByToken(resetToken.getToken());
        }
        resetTokenRepository.save(resetToken);

        composeAndSendResetLink(user.getEmail(), nonHashedToken);
    }

    @Override
    public UserTokenState refreshAuthenticationToken(HttpServletRequest request) {
        String refreshToken = tokenUtils.getToken(request);
        String username = this.tokenUtils.getUsernameFromToken(refreshToken);
        User user = (User) loadUserByUsername(username);

        if (this.tokenUtils.canTokenBeRefreshed(refreshToken, user.getLastPasswordResetDate())) {
            String newToken = tokenUtils.refreshToken(refreshToken, user);
            int expiresIn = tokenUtils.getExpiredIn();
            return new UserTokenState(newToken, expiresIn, refreshToken);
        } else {
            throw new InvalidUserDataException("Token can not be refreshed", HttpStatus.BAD_REQUEST);
        }
    }

    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    private String getTokenHash(String token) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        digest.reset();
        digest.update(token.getBytes());
        return String.format("%040x", new BigInteger(1, digest.digest()));
    }

    private void composeAndSendEmail(String recipientEmail) {
        String subject = "Reset your password";
        StringBuilder sb = new StringBuilder();
        sb.append("You got this email because you requested a password reset.");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("Your account is not active, therefore your password cannot be reset. ");
        sb.append(
                "To activate it, use the previously received activation link or change the previously received generic password.");
        String text = sb.toString();

        emailNotificationService.sendEmail(recipientEmail, subject, text);
    }

    private void composeAndSendResetLink(String recipientEmail, String token) {
        String subject = "Reset your password";
        StringBuilder sb = new StringBuilder();
        sb.append("You got this email because you requested a password reset.");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("To reset your password click the following link:");
        sb.append(System.lineSeparator());
        sb.append(getLocalhostURL());
        sb.append("reset-password?t=");
        sb.append(token);
        String text = sb.toString();

        emailNotificationService.sendEmail(recipientEmail, subject, text);
    }

    private String getLocalhostURL() {
        return environment.getProperty("LOCALHOST_URL");
    }

    @Autowired
    public UserServiceImpl(UserRepository userRepository, TokenUtils tokenUtils, RoleRepository roleRepository,
            ResetTokenRepository resetTokenRepository, EmailNotificationService emailNotificationService,
            Environment environment, LoginAttemptService loginAttemptService, HttpServletRequest request) {
        this.userRepository = userRepository;
        this.tokenUtils = tokenUtils;
        this.roleRepository = roleRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.emailNotificationService = emailNotificationService;
        this.environment = environment;
        this.loginAttemptService = loginAttemptService;
        this.request = request;
    }
}
