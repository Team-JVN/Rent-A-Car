package jvn.RentACar.dto.both;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class RentReportDTO {

    private Long id;

    private String comment;

    @NotNull(message = "Rent request is null.")
    private RentInfoDTO rentInfo;

    private Double additionalCost;

    private Integer madeMileage;

}
