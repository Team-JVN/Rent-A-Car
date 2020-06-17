package jvn.Advertisements.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {

    @Positive(message = "Id must be positive.")
    private Long id;

    @NotBlank(message = "Email is empty.")
    @Email(message = "Email is not valid.")
    private String email;

}
