package jvn.Advertisements.dto.response;

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
public class GearboxTypeDTO {
    @NotNull(message = "Id is null.")
    @Positive(message = "Id must be positive.")
    private Long id;

    @NotBlank(message = "Name is empty.")
    @Pattern(regexp = "^(([A-Za-zÀ-ƒ0-9]+[ ]?|[a-zÀ-ƒ0-9]+['-]?){0,4})$", message = "Name is not valid.")
    private String name;

}