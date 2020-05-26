package jvn.Users.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserTokenState {

    private String accessToken;

    private Long expiresIn;

    private String refreshToken;

    public UserTokenState(String accessToken, long expiresIn, String refreshToken) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
    }

}
