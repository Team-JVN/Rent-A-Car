package jvn.Advertisements.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {

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

    @NotEmpty
    private List<String> permissions;

    private Boolean canCreateRentRequests;

    private Boolean canCreateComments;
}