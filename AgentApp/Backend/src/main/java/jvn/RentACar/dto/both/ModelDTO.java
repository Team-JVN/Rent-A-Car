package jvn.RentACar.dto.both;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
public class ModelDTO {

    private Long id;

    @NotEmpty(message = "Name is empty.")
    private String name;
}
