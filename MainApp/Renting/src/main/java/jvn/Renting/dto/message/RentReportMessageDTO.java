package jvn.Renting.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentReportMessageDTO {

    @NotNull(message = "Advertisement is null")
    @Positive(message = "Advertisement's id must be positive.")
    private Long advertisementId;

    private Integer madeMileage;
}
