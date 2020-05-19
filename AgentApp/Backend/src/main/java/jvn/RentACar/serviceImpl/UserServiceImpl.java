package jvn.RentACar.serviceImpl;

import jvn.RentACar.exceptionHandler.BlockedUserException;
import jvn.RentACar.exceptionHandler.InvalidUserDataException;
import jvn.RentACar.model.*;
import jvn.RentACar.repository.RoleRepository;
import jvn.RentACar.repository.UserRepository;
import jvn.RentACar.security.TokenUtils;
import jvn.RentACar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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

@Service
public class UserServiceImpl implements UserDetailsService, UserService {

    private UserRepository userRepository;

    public TokenUtils tokenUtils;

    private RoleRepository roleRepository;

    private LoginAttemptService loginAttemptService;

    private HttpServletRequest request;

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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException,BlockedUserException {
        String ip = getClientIP();
        if (loginAttemptService.isBlocked(ip)) {
            throw new BlockedUserException(String.format("You are blocked so you can not sign in.", username),HttpStatus.BAD_REQUEST);
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
        if (xfHeader == null){
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    @Autowired
    public UserServiceImpl(UserRepository userRepository, TokenUtils tokenUtils, RoleRepository roleRepository,LoginAttemptService loginAttemptService,HttpServletRequest request) {
        this.userRepository = userRepository;
        this.tokenUtils = tokenUtils;
        this.roleRepository = roleRepository;
        this.loginAttemptService=loginAttemptService;
        this.request=request;
    }
}
