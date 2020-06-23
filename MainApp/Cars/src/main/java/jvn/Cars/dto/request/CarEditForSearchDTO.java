package jvn.Cars.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CarEditForSearchDTO {
    @NotNull(message = "Id is null.")
    @Positive(message = "Id must be positive.")
    private Long id;

    @NotNull(message = "Mileage in km is null.")
    @Min(value = 0, message = "Mileage must be positive number.")
    private Integer mileageInKm;

    @NotNull(message = "Number of kid's seats in km is null.")
    @Min(value = 0, message = "Number of kid's seats must be positive number")
    @Max(value = 3, message = "Max number of kid's seats is 3.")
    private Integer kidsSeats;

    @NotNull(message = "Available tracking is null.")
    private Boolean availableTracking;

    @NotEmpty(message = "Pictures are null.")
    private List<String> pictures;

}
