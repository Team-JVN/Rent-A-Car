package jvn.RentACar.serviceImpl;

import jvn.RentACar.dto.request.ChangePasswordDTO;
import jvn.RentACar.enumeration.AgentStatus;
import jvn.RentACar.enumeration.ClientStatus;
import jvn.RentACar.exceptionHandler.InvalidUserDataException;
import jvn.RentACar.model.*;
import jvn.RentACar.repository.RoleRepository;
import jvn.RentACar.repository.UserRepository;
import jvn.RentACar.security.JwtAuthenticationRequest;
import jvn.RentACar.security.TokenUtils;
import jvn.RentACar.service.UserService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {

    private UserRepository userRepository;

    private TokenUtils tokenUtils;

    private AuthenticationManager authenticationManager;

    private RoleRepository roleRepository;

    private PasswordEncoder passwordEncoder;

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
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with email '%s'.", username));
        } else {
            return user;
        }
    }

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

    @Autowired
    public UserServiceImpl(UserRepository userRepository, TokenUtils tokenUtils,
            AuthenticationManager authenticationManager, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenUtils = tokenUtils;
        this.authenticationManager = authenticationManager;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
}
