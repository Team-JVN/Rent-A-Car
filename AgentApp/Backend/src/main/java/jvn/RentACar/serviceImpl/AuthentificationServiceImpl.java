package jvn.RentACar.serviceImpl;

import jvn.RentACar.dto.request.ChangePasswordDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class AuthentificationServiceImpl implements AuthentificationService {

    public TokenUtils tokenUtils;

    private AuthenticationManager authenticationManager;

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

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
    public void changePassword(ChangePasswordDTO changePasswordDTO) throws NullPointerException {
        User user = userRepository.findByEmail(changePasswordDTO.getEmail());

        if (user instanceof Client) {
            ((Client) user).setStatus(ClientStatus.ACTIVE);
        } else if (user instanceof Agent) {
            ((Agent) user).setStatus(AgentStatus.ACTIVE);
        }

        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new InvalidUserDataException("Invalid password.", HttpStatus.BAD_REQUEST);
        }
        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        user.setLastPasswordResetDate(new Timestamp(DateTime.now().getMillis()));
        userRepository.save(user);
    }

    @Autowired
    public AuthentificationServiceImpl(TokenUtils tokenUtils, AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.tokenUtils = tokenUtils;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
}
