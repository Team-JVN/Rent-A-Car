package jvn.Cars.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
public class CreateGearboxTypeDTO {

    @NotBlank(message = "Name is empty.")
    @Pattern(regexp = "^(([A-Za-zÀ-ƒ0-9]+[ ]?|[a-zÀ-ƒ0-9]+['-]?){0,4})$", message = "Name is not valid.")
    private String name;
}
