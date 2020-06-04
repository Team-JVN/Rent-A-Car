package jvn.Renting.dto.both;

import jvn.Renting.model.RentInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
public class RentReportDTO {

    @Positive(message = "Id must be positive.")
    private Long id;

    private String comment;

    @NotBlank(message = "Rent info is empty.")
    private RentInfoDTO rentInfo;

    private Double additionalCost;

    @Positive(message = "Made mileage is not a positive number.")
    private Integer madeMileage;
}
