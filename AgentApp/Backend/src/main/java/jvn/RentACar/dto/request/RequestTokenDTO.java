package jvn.RentACar.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class RequestTokenDTO {

    @NotBlank(message = "Email is empty.")
    @Email(message = "Email is not valid.")
    private String email;

}
