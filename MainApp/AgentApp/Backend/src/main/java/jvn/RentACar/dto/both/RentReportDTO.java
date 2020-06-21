package jvn.RentACar.dto.both;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
public class RentReportDTO {

    @Positive(message = "Id must be positive.")
    private Long id;

    @Pattern(regexp = "^[\\p{N}\\p{L}\\p{Sc}@ !()-,.:;/'\"&*=+%]+$", message = "Comment is not valid.")
    private String comment;

//    @NotNull(message = "Rent request is null.")
//    private RentInfoDTO rentInfo;

    @Positive(message = "Additional cost is not a positive number.")
    private Double additionalCost;

    @NotNull(message = "Made mileage is null.")
    @Positive(message = "Made mileage is not a positive number.")
    private Integer madeMileage;

}
