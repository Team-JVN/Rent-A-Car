package jvn.RentACar.dto.both;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class ClientDTO {

    private Long id;

    @NotEmpty(message = "Name is empty.")
    private String name;

    @NotEmpty(message = "Email is empty.")
    @Email(message = "Email is not valid.")
    private String email;

    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$")
    private String password;

    @NotEmpty(message = "Address is empty.")
    private String address;

    @NotEmpty(message = "PhoneNumber is empty.")
    @Size(min = 9, max = 10)
    @Pattern(regexp = "0[0-9]+")
    private String phoneNumber;
}
