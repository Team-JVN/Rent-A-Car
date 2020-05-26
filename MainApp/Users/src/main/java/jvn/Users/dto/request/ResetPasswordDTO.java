package jvn.Users.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
public class ResetPasswordDTO {

    @NotBlank(message = "New password is empty.")
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,64}$",
            message = "Password must be between 8 and 64 characters long and must contain at least 1 lowercase, 1 uppercase letter and 1 number.")
    private String newPassword;

}