package jvn.RentACar.dto.response;

import jvn.RentACar.model.UserTokenState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
public class LoggedInUserDTO {
    @Positive(message = "Id must be positive.")
    private Long id;

    @Email(message = "Email is not valid.")
    private String email;

    @Pattern(regexp = "(ROLE_AGENT|ROLE_CLIENT)$",message = "Role is not valid.")
    private String role;

    private UserTokenState userTokenState;

    public LoggedInUserDTO(Long id, String email, String role, UserTokenState userTokenState) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.userTokenState = userTokenState;
    }
}
