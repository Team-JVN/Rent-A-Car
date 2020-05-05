package jvn.RentACar.dto.both;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class CarDTO {
    @NotNull(message = "Id is null.")
    private Long id;

    @NotNull(message = "Make is empty.")
    private MakeDTO make;

    @NotNull(message = "Model is empty.")
    private ModelDTO model;

    @NotNull(message = "Fuel type is null.")
    private FuelTypeDTO fuelType;

    @NotNull(message = "Gerbox type is null.")
    private GearboxTypeDTO gearBoxType;

    @NotNull(message = "Body style is null.")
    private BodyStyleDTO bodyStyle;

    @NotNull(message = "Mileage in km is null.")
    @Min(0)
    private Integer mileageInKm;

    @NotNull(message = "Number of kid's seats in km is null.")
    @Min(0)
    @Max(3)
    private Integer kidsSeats;

    @NotNull(message = "Available tracking is null.")
    private Boolean availableTracking;

    private Double avgRating;

    private Integer commentsCount;
}
