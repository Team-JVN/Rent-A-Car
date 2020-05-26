package jvn.Users.dto.both;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
public class PermissionDTO {

    @NotNull(message = "Id is null.")
    @Positive(message = "Id must be positive.")
    private Long id;

    @NotBlank(message = "Name is empty.")
    @Pattern(regexp = "^(([A-Z]+[_]?)*([A-Z]+))$", message = "Name is not valid.")
    private String name;
}
