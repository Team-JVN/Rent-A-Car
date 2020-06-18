package jvn.Users.serviceImpl;

import jvn.Users.dto.response.UserInfoDTO;
import jvn.Users.dto.message.Log;
import jvn.Users.enumeration.AdminStatus;
import jvn.Users.enumeration.AgentStatus;
import jvn.Users.enumeration.ClientStatus;
import jvn.Users.exceptionHandler.InvalidUserDataException;
import jvn.Users.model.*;
import jvn.Users.producer.LogProducer;
import jvn.Users.repository.ResetTokenRepository;
import jvn.Users.repository.RoleRepository;
import jvn.Users.repository.UserRepository;
import jvn.Users.security.TokenUtils;
import jvn.Users.service.EmailNotificationService;
import jvn.Users.service.UserService;
import jvn.Users.utils.IPAddressProvider;
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

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private Environment environment;

    private UserRepository userRepository;

    public TokenUtils tokenUtils;

    private RoleRepository roleRepository;

    private LoginAttemptService loginAttemptService;

    private HttpServletRequest request;

    private ResetTokenRepository resetTokenRepository;

    private EmailNotificationService emailNotificationService;

    private LogProducer logProducer;

    private IPAddressProvider ipAddressProvider;

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
    public void generateResetToken(String email) throws NoSuchAlgorithmException {
        User user = findByEmail(email);
        if (user == null) {
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "RPW", String.format("User from %s tried to reset password for an unregistered user", ipAddressProvider.get())));
            return;
        }

        if (user instanceof Client && ((Client) user).getStatus() != ClientStatus.ACTIVE) {
            composeAndSendEmail(user.getEmail());
            return;
        } else if (user instanceof Agent && ((Agent) user).getStatus() != AgentStatus.ACTIVE) {
            composeAndSendEmail(user.getEmail());
            return;
        } else if (user instanceof Admin && ((Admin) user).getStatus() != AdminStatus.ACTIVE) {
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
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "REF", String.format("User from %s tried to refresh token manually", ipAddressProvider.get())));
            throw new InvalidUserDataException("Token can not be refreshed", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public User verify() {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(currentUser.getName());
    }

    @Override
    public UserInfoDTO getByEmail(String email) {
        User user = findByEmail(email);
        if (user == null) {
            return null;
        }
        if (user instanceof Client) {
            Client client = (Client) user;
            return new UserInfoDTO(user.getId(), user.getName(), user.getEmail(), user.getRole().getName(), client.getCanCreateRentRequests(), client.getCanCreateComments());
        } else {
            return new UserInfoDTO(user.getId(), user.getName(), user.getEmail(), user.getRole().getName(), null, null);
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
                           Environment environment, LoginAttemptService loginAttemptService, HttpServletRequest request,
                           LogProducer logProducer, IPAddressProvider ipAddressProvider) {
        this.userRepository = userRepository;
        this.tokenUtils = tokenUtils;
        this.roleRepository = roleRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.emailNotificationService = emailNotificationService;
        this.environment = environment;
        this.loginAttemptService = loginAttemptService;
        this.request = request;
        this.logProducer = logProducer;
        this.ipAddressProvider = ipAddressProvider;
    }
}

