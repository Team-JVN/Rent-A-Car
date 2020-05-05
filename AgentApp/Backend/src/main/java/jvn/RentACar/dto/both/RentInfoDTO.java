package jvn.RentACar.dto.both;

import jvn.RentACar.dto.response.AdvertisementWithPicturesDTO;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NotEmpty
public class RentInfoDTO {

    private Long id;

    @NotEmpty(message = "Date and time from is empty.")
    private String dateTimeFrom;

    @NotEmpty(message = "Date and time to is empty.")
    private String dateTimeTo;

    private Boolean optedForCDW;

    @NotNull(message = "Advertisement is null.")
    private AdvertisementWithPicturesDTO advertisement;

//    private RentReportDTO rentReport;
}
