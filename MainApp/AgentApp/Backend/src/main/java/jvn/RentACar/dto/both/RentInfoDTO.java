package jvn.RentACar.dto.both;

import jvn.RentACar.dto.response.AdvertisementWithPicturesDTO;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
@NotEmpty
public class RentInfoDTO {

    @Positive(message = "Id must be positive.")
    private Long id;

    @NotBlank(message = "Date and time from is empty.")
    @Pattern(regexp = "^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]$",message = "Date and time from are not validly formatted")
    @Future(message = "Date time from must be in the future")
    private String dateTimeFrom;

    @NotBlank(message = "Date and time to is empty.")
    @Pattern(regexp = "^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]$",message = "DDate and time to are not validly formatted")
    @Future(message = "Date time from must be in the future")
    private String dateTimeTo;

    private Boolean optedForCDW;

    @NotNull(message = "Advertisement is null.")
    private AdvertisementWithPicturesDTO advertisement;

//    private RentReportDTO rentReport;
}