package jvn.RentACar.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordDTO {

    @NotBlank(message = "Email is empty.")
    @Email(message = "Email is invalid.")
    private String email;

    @NotBlank(message = "Old password empty.")
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$",message = " Password must be at least 8 characters long and must contain at least 1 lowercase, 1 uppercase letter and 1 number.")
    private String oldPassword;

    @NotBlank(message = "New password empty.")
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$",message = " Password must be at least 8 characters long and must contain at least 1 lowercase, 1 uppercase letter and 1 number.")
    private String newPassword;

}
