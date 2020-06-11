package jvn.Cars.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Cars.dto.request.UserDTO;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String authToken = getToken(request);
        UserDetails userDetails = getUser(request);
        TokenBasedAuthentication authentication = new TokenBasedAuthentication(userDetails);
        authentication.setToken(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }

    public String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Auth");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    public User getUser(HttpServletRequest request) {
        UserDTO userDTO = stringToObject(request.getHeader("user"));
        User user = new User();
        user.setId(userDTO.getId());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        user.setPermissions(userDTO.getPermissions());
        return user;
    }

    private UserDTO stringToObject(String user) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(user, UserDTO.class);
        } catch (JsonProcessingException e) {
            //TODO: Add to log and delete return null;
            return null;
        }
    }
}
