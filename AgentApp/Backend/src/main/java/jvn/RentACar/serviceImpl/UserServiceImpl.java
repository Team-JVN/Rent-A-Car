package jvn.RentACar.serviceImpl;

import jvn.RentACar.dto.request.ChangePasswordDTO;
import jvn.RentACar.dto.response.LoggedInUserDTO;
import jvn.RentACar.exceptionHandler.InvalidUserDataException;
import jvn.RentACar.model.Agent;
import jvn.RentACar.model.Authority;
import jvn.RentACar.model.User;
import jvn.RentACar.model.UserTokenState;
import jvn.RentACar.repository.AuthorityRepository;
import jvn.RentACar.repository.UserRepository;
import jvn.RentACar.security.JwtAuthenticationRequest;
import jvn.RentACar.security.TokenUtils;
import jvn.RentACar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {

    private UserRepository userRepository;


    private TokenUtils tokenUtils;

    private AuthenticationManager authenticationManager;

    private AuthorityRepository authorityRepository;

    private PasswordEncoder passwordEncoder;

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = false)
    @EventListener(ApplicationReadyEvent.class)
    public void insertAfterStartup() {
        Authority authorityAgent = new Authority();
        authorityAgent.setName("ROLE_AGENT");
        if (authorityRepository.findByName(authorityAgent.getName()) != null) {
            return;
        }
        authorityRepository.save(authorityAgent);

        Authority authorityClient = new Authority();
        authorityClient.setName("ROLE_CLIENT");
        if (authorityRepository.findByName(authorityClient.getName()) != null) {
            return;
        }
        authorityRepository.save(authorityClient);
        //Rentacar1
        Agent agent = new Agent("Rent-A-Car agency", "rentacar@maildrop.cc", "$2a$10$34ippcUyKNhjxY8o5yDVBO47eOXdtXzC1LqPPO4UlxJgdbdo/lcZe", "Beograd", "11111111");
        if (userRepository.findByEmail(agent.getEmail()) != null) {
            return;
        }

        Authority auth = this.authorityRepository.findByName("ROLE_AGENT");
        Set<Authority> authorities = new HashSet<>();
        authorities.add(auth);
        agent.setAuthorities(authorities);
        userRepository.save(agent);
    }

    @Override
    public Set<Authority> findByName(String name) {
        Authority auth = this.authorityRepository.findByName(name);
        Set<Authority> authorities = new HashSet<>();
        authorities.add(auth);
        return authorities;
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
    public LoggedInUserDTO login(JwtAuthenticationRequest authenticationRequest) {
        final Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();
        String jwt = tokenUtils.generateToken(user.getUsername());
        int expiresIn = tokenUtils.getExpiredIn();
        Authority authority = (Authority) user.getAuthorities().toArray()[0];
        String role = authority.getName();
        return new LoggedInUserDTO(user.getId(), user.getEmail(), role, new UserTokenState(jwt, expiresIn));
    }

    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO) throws NullPointerException {
        User user = userRepository.findByEmail(changePasswordDTO.getEmail());

        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new InvalidUserDataException("Invalid password.", HttpStatus.BAD_REQUEST);
        }
        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public Agent getLoginAgent() {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();

        return (Agent) userRepository.findByEmail(currentUser.getName());
    }

    @Autowired
    public UserServiceImpl(UserRepository userRepository, TokenUtils tokenUtils, AuthenticationManager authenticationManager,
                           AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenUtils = tokenUtils;
        this.authenticationManager = authenticationManager;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }
}
