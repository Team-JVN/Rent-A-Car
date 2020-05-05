package jvn.RentACar.dto.both;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
public class PicturesDTO {

    private Long id;

    @NotEmpty(message = "Data is empty.")
    private String data;
}
