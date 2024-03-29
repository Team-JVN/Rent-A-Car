package jvn.RentACar.serviceImpl;

import jvn.RentACar.dto.request.ChangePasswordDTO;
import jvn.RentACar.dto.request.ResetPasswordDTO;
import jvn.RentACar.dto.response.CheckPassDTO;
import jvn.RentACar.enumeration.AgentStatus;
import jvn.RentACar.enumeration.ClientStatus;
import jvn.RentACar.exceptionHandler.BlockedUserException;
import jvn.RentACar.exceptionHandler.InvalidTokenException;
import jvn.RentACar.exceptionHandler.InvalidUserDataException;
import jvn.RentACar.model.*;
import jvn.RentACar.repository.ResetTokenRepository;
import jvn.RentACar.repository.UserRepository;
import jvn.RentACar.security.JwtAuthenticationRequest;
import jvn.RentACar.security.TokenUtils;
import jvn.RentACar.service.AuthentificationService;
import jvn.RentACar.service.LogService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class AuthentificationServiceImpl implements AuthentificationService {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    public TokenUtils tokenUtils;

    private AuthenticationManager authenticationManager;

    private UserRepository userRepository;

    private ResetTokenRepository resetTokenRepository;

    private PasswordEncoder passwordEncoder;

    private final RestTemplate restTemplate;

    private HttpServletRequest request;

    private ChangePasswordAttemptService changePasswordAttemptService;

    private LogService logService;

    @Override
    public UserTokenState login(JwtAuthenticationRequest authenticationRequest) {
        final Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();
        String jwt = tokenUtils.generateToken(user.getUsername(), user.getRole().getName(),
                user.getRole().getPermissions());
        String refreshJwt = tokenUtils.generateRefreshToken(user.getUsername());
        int expiresIn = tokenUtils.getExpiredIn();

        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "LGN", String.format("User %s successfully logged in", user.getId())));
        return new UserTokenState(jwt, expiresIn, refreshJwt);
    }

    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO)
            throws NullPointerException, NoSuchAlgorithmException {
        String ip = getClientIP();
        if (changePasswordAttemptService.isBlocked(ip)) {
            logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CPW", String.format("User from %s is blocked.", getClientIP())));
            throw new BlockedUserException(
                    "You tried to log in too many times. Your account wil be blocked for the next 24 hours.",
                    HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByEmail(changePasswordDTO.getEmail());

        if (user instanceof Client) {
            ((Client) user).setStatus(ClientStatus.ACTIVE);
        } else if (user instanceof Agent) {
            ((Agent) user).setStatus(AgentStatus.ACTIVE);
        }

        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
            changePasswordAttemptService.changePassFailed();
            throw new InvalidUserDataException("Invalid email or password. Please try again.", HttpStatus.BAD_REQUEST);
        }
        checkPassword(changePasswordDTO.getNewPassword());
        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        user.setLastPasswordResetDate(new Timestamp(DateTime.now().getMillis()));
        userRepository.save(user);
        changePasswordAttemptService.changePassSucceeded();
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CPW", String.format("User %s successfully changed password", user.getId())));
    }

    @Override
    public void checkPassword(String password) throws NoSuchAlgorithmException {
        String url = "https://api.badpasswordcheck.com/check/" + getHashValue(password);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "1c535167-54e8-4a11-9e2d-21fda54292e1");
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<CheckPassDTO> response = this.restTemplate.exchange(url, HttpMethod.GET, request,
                CheckPassDTO.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            CheckPassDTO checkPassDTO = response.getBody();
            if (checkPassDTO.getFound()) {
                throw new InvalidUserDataException("This password isn't secure. Please choose another one.",
                        HttpStatus.BAD_REQUEST);
            }
        } else {
            throw new InvalidUserDataException("Password can not be checked. Please try again.",
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void resetPassword(String token, ResetPasswordDTO resetPasswordDTO)
            throws NullPointerException, NoSuchAlgorithmException {
        ResetToken resetToken = resetTokenRepository.findByTokenAndExpiryDateTimeAfter(getTokenHash(token),
                LocalDateTime.now());
        if (resetToken == null) {
            throw new InvalidTokenException("This reset token is invalid or expired.", HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByEmail(resetToken.getUser().getEmail());
        checkPassword(resetPasswordDTO.getNewPassword());
        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        user.setLastPasswordResetDate(new Timestamp(DateTime.now().getMillis()));
        userRepository.save(user);

        resetTokenRepository.deleteById(resetToken.getId());
    }

    @Override
    public boolean userIsNeverLoggedIn(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return false;
        }
        if (user instanceof Agent) {
            return ((Agent) user).getStatus().equals(AgentStatus.INACTIVE);
        } else {
            return ((Client) user).getStatus().equals(ClientStatus.NEVER_LOGGED_IN);
        }
    }

    private String getHashValue(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        digest.update(password.getBytes(StandardCharsets.UTF_8));
        return String.format("%040x", new BigInteger(1, digest.digest()));
    }

    private String getTokenHash(String token) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        digest.reset();
        digest.update(token.getBytes());
        return String.format("%040x", new BigInteger(1, digest.digest()));
    }

    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        String s = xfHeader.split(",")[0];
        return s;
    }

    @Autowired
    public AuthentificationServiceImpl(TokenUtils tokenUtils, AuthenticationManager authenticationManager,
                                       UserRepository userRepository, PasswordEncoder passwordEncoder, RestTemplateBuilder restTemplateBuilder,
                                       HttpServletRequest request, ChangePasswordAttemptService changePasswordAttemptService,
                                       ResetTokenRepository resetTokenRepository, LogService logService) {
        this.tokenUtils = tokenUtils;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.restTemplate = restTemplateBuilder.build();
        this.request = request;
        this.changePasswordAttemptService = changePasswordAttemptService;
        this.resetTokenRepository = resetTokenRepository;
        this.logService = logService;
    }
}
