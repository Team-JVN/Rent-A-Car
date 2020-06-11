package jvn.RentACar.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordDTO {

    @NotBlank(message = "Email is empty.")
    @Email(message = "Email is invalid.")
    private String email;

    @NotBlank(message = "Old password is empty.")
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[_#?!@$%^&*-.,:;]).{10,64}$", message = "Password must be between 10 and 64 characters long and must contain a number, a special character, a lowercase and an uppercase letter.")
    private String oldPassword;

    @NotBlank(message = "New password is empty.")
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[_#?!@$%^&*-.,:;]).{10,64}$", message = "Password must be between 10 and 64 characters long and must contain a number, a special character, a lowercase and an uppercase letter.")
    private String newPassword;

}
