package jvn.RentACar.security;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

public class JwtAuthenticationRequest {
    @Email
    private String username;

    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,64}$", message = "Password must be between 8 and 64 characters long and must contain at least 1 lowercase, 1 uppercase letter and 1 number.")
    private String password;

    public JwtAuthenticationRequest() {
        super();
    }

    public JwtAuthenticationRequest(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
