package jvn.RentACar.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordDTO {

    @NotEmpty(message = "Email is empty.")
    @Email(message = "Email is invalid.")
    private String email;

    @NotEmpty(message = "Old password empty.")
    private String oldPassword;

    @NotEmpty(message = "New password empty.")
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$")
    private String newPassword;

}
