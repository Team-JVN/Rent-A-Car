package jvn.RentACar.serviceImpl;

import jvn.RentACar.dto.request.ChangePasswordDTO;
import jvn.RentACar.dto.response.CheckPassDTO;
import jvn.RentACar.enumeration.AgentStatus;
import jvn.RentACar.enumeration.ClientStatus;
import jvn.RentACar.exceptionHandler.InvalidUserDataException;
import jvn.RentACar.model.Agent;
import jvn.RentACar.model.Client;
import jvn.RentACar.model.User;
import jvn.RentACar.model.UserTokenState;
import jvn.RentACar.repository.UserRepository;
import jvn.RentACar.security.JwtAuthenticationRequest;
import jvn.RentACar.security.TokenUtils;
import jvn.RentACar.service.AuthentificationService;
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

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

@Service
public class AuthentificationServiceImpl implements AuthentificationService {

    public TokenUtils tokenUtils;

    private AuthenticationManager authenticationManager;

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private final RestTemplate restTemplate;

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

        return new UserTokenState(jwt, expiresIn, refreshJwt);
    }

    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO) throws NullPointerException, UnsupportedEncodingException, NoSuchAlgorithmException {
        User user = userRepository.findByEmail(changePasswordDTO.getEmail());

        if (user instanceof Client) {
            ((Client) user).setStatus(ClientStatus.ACTIVE);
        } else if (user instanceof Agent) {
            ((Agent) user).setStatus(AgentStatus.ACTIVE);
        }

        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new InvalidUserDataException("Invalid password.", HttpStatus.BAD_REQUEST);
        }
        checkPassword(changePasswordDTO.getNewPassword());
        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        user.setLastPasswordResetDate(new Timestamp(DateTime.now().getMillis()));
        userRepository.save(user);
    }

    @Override
    public void checkPassword(String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String url = "https://api.badpasswordcheck.com/check/" + getHashValue(password);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "1c535167-54e8-4a11-9e2d-21fda54292e1");
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<CheckPassDTO> response = this.restTemplate.exchange(url, HttpMethod.GET, request, CheckPassDTO.class);
        if(response.getStatusCode() == HttpStatus.OK) {
            CheckPassDTO checkPassDTO = response.getBody();
            if(checkPassDTO.getFound()){
                throw new InvalidUserDataException("This password isn't secure. Please choose another one.", HttpStatus.BAD_REQUEST);
            }
        }else{
            throw new InvalidUserDataException("Password can not be check. Please try again.", HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    public boolean userIsNeverLoggedIn(String email) {
        User user = userRepository.findByEmail(email);
        if(user instanceof Agent){
            if(((Agent) user).getStatus().equals(AgentStatus.INACTIVE)){
                return true;
            }
        }else {
            if(((Client) user).getStatus().equals(ClientStatus.NEVER_LOGGED_IN)){
                return true;
            }
        }
        return false;
    }

    private String getHashValue(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        digest.update(password.getBytes("utf8"));
        return String.format("%040x", new BigInteger(1, digest.digest()));
    }

    @Autowired
    public AuthentificationServiceImpl(TokenUtils tokenUtils, AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, RestTemplateBuilder restTemplateBuilder) {
        this.tokenUtils = tokenUtils;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.restTemplate = restTemplateBuilder.build();
    }
}
