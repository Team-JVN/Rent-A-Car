package jvn.RentACar.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class CarEditDTO {
    @NotNull(message = "Id is null.")
    private Long id;

    @NotNull(message = "Mileage in km is null.")
    @Min(0)
    private Integer mileageInKm;

    @NotNull(message = "Number of kid's seats in km is null.")
    @Min(0)
    @Max(3)
    private Integer kidsSeats;

    @NotNull(message = "Available tracking is null.")
    private Boolean availableTracking;
}
