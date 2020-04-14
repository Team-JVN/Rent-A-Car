package jvn.RentACar.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
public class CreateGearBoxTypeDTO {

    @NotEmpty(message = "Name is empty.")
    private String name;
}
