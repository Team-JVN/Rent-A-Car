package jvn.RentACar.dto.request;

import jvn.RentACar.dto.both.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class CreateCarDTO implements Serializable {
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
    @Min(value = 0,message = "Mileage must be positive number.")
    private Integer mileageInKm;

    @NotNull(message = "Number of kid's seats in km is null.")
    @Min(value = 0,message = "Number of kid's seats must be positive number")
    @Max(value = 3,message = "Max number of kid's seats is 3.")
    private Integer kidsSeats;

    @NotNull(message = "Available tracking is null.")
    private Boolean availableTracking;
}
