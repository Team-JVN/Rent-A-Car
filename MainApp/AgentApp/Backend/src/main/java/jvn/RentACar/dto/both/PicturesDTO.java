package jvn.RentACar.dto.both;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
public class PicturesDTO {
    @Positive(message = "Id must be positive.")
    private Long id;

    @NotBlank(message = "Data is empty.")
    @Pattern(regexp = "^(data:image/[^;]+;base64[^\"]+)$",message = "Data for representing picture is not valid.")
    private String data;
}
