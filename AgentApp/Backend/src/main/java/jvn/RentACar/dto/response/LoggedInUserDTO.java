package jvn.RentACar.dto.response;

import jvn.RentACar.model.UserTokenState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoggedInUserDTO {
    private Long id;

    private String email;

    private String role;

    private UserTokenState userTokenState;

    public LoggedInUserDTO(Long id, String email, String role, UserTokenState userTokenState) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.userTokenState = userTokenState;
    }
}
