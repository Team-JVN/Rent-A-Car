package jvn.Renting.dto.both;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
public class RentReportDTO {

    @Positive(message = "Id must be positive.")
    private Long id;

    @NotBlank(message = "Comment is empty.")
    private String comment;

    private Double additionalCost;

    @Positive(message = "Made mileage is not a positive number.")
    private Integer madeMileage;
}
