package jvn.Users.dto.both;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class RoleDTO {

    @NotNull(message = "Id is null.")
    @Positive(message = "Id must be positive.")
    private Long id;

    @NotBlank(message = "Name is empty.")
    @Pattern(regexp = "^(([A-Z]+[_]?)*([A-Z]+))$", message = "Name is not valid.")
    private String name;

    @NotEmpty(message = "Set of permissions is empty.")
    private Set<PermissionDTO> permissions;
}
