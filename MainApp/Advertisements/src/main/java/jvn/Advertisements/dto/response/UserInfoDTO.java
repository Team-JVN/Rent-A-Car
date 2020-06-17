package jvn.Advertisements.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {

    @Positive(message = "Id must be positive.")
    private Long id;

    @NotBlank(message = "Name is empty.")
    @Pattern(regexp = "^(([A-Za-zÀ-ƒ]+[.]?[ ]?|[a-zÀ-ƒ]+['-]?){0,4})$", message = "Name is not valid.")
    private String name;

    @NotBlank(message = "Email is empty.")
    @Email(message = "Email is not valid.")
    private String email;

    @NotBlank(message = "Role is empty.")
    @Pattern(regexp = "(?i)(ROLE_AGENT|ROLE_CLIENT|ROLE_ADMIN)$", message = "Role is not valid.")
    private String role;

    private Boolean canCreateRentRequests;

    private Boolean canCreateComments;

}
