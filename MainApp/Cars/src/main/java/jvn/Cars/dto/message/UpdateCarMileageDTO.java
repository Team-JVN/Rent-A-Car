package jvn.Cars.dto.message;

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
public class UpdateCarMileageDTO {

    @NotNull(message = "Car is null")
    @Positive(message = "Car's id must be positive.")
    private Long carId;

    private Integer madeMileage;
}
