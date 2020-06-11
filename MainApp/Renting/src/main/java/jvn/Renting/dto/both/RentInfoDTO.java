package jvn.Renting.dto.both;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class RentInfoDTO {

    @Positive(message = "Id must be positive.")
    private Long id;

    @NotBlank(message = "Date and time from is empty.")
    @Pattern(regexp = "^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]$", message = "Date and time from are not validly formatted.")
    private String dateTimeFrom;

    @NotBlank(message = "Date and time to is empty.")
    @Pattern(regexp = "^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]$", message = "Date and time to are not validly formatted.")
    private String dateTimeTo;

    private Boolean optedForCDW;

    @NotNull(message = "Advertisement is null.")
    private AdvertisementDTO advertisement;

    private Boolean inBundle;

    private Set<CommentDTO> comments;

}
