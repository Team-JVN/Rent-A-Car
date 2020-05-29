package jvn.Users.dto.both;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
public class AgentDTO {

    @Positive(message = "Id must be positive.")
    private Long id;

    @NotBlank(message = "Name is empty.")
    @Pattern(regexp = "^(([A-Za-zÀ-ƒ]+[.]?[ ]?|[a-zÀ-ƒ]+['-]?){0,4})$", message = "Name is not valid.")
    private String name;

    @NotBlank(message = "Email is empty.")
    @Email(message = "Email is not valid.")
    private String email;

    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,64}$", message = "Password must be between 8 and 64 characters long and must contain at least 1 lowercase, 1 uppercase letter and 1 number.")
    private String password;

    @NotBlank(message = "Address is empty.")
    @Pattern(regexp = "^([#.0-9a-zA-ZÀ-ƒ,-\\/]+[ ]?){1,10}$", message = "Address is not valid.")
    private String address;

    @NotBlank(message = "PhoneNumber is empty.")
    @Size(min = 9, max = 10, message = "Phone number can contain between 9 and 10 digits.")
    @Pattern(regexp = "0[0-9]+", message = " Phone number must begin with 0 and can contain digits only.")
    private String phoneNumber;

    private String taxIdNumber;

}
