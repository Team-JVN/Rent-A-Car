package jvn.Cars.dto.both;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
public class CarDTO {

    @NotNull(message = "Id is null.")
    @Positive(message = "Id must be positive.")
    private Long id;

    @NotNull(message = "Make is empty.")
    private MakeDTO make;

    @NotNull(message = "Model is empty.")
    private ModelDTO model;

    @NotNull(message = "Fuel type is null.")
    private FuelTypeDTO fuelType;

    @NotNull(message = "Gearbox type is null.")
    private GearboxTypeDTO gearBoxType;

    @NotNull(message = "Body style is null.")
    private BodyStyleDTO bodyStyle;

    @NotNull(message = "Mileage is null.")
    @Min(value = 0, message = "Mileage must be positive number.")
    private Integer mileageInKm;

    @NotNull(message = "Number of kid's seats in km is null.")
    @Min(value = 0, message = "Number of kid's seats must be positive number")
    @Max(value = 3, message = "Max number of kid's seats is 3.")
    private Integer kidsSeats;

    @NotNull(message = "Available tracking is null.")
    private Boolean availableTracking;

    @PositiveOrZero(message = "Average rating is not a positive number.")
    private Double avgRating;

    @PositiveOrZero(message = "Comment count is not a positive number.")
    private Integer commentsCount;
}
