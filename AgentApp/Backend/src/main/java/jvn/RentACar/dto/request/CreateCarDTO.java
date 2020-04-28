package jvn.RentACar.dto.request;

import jvn.RentACar.dto.both.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class CreateCarDTO implements Serializable {
    @NotEmpty(message = "Make is empty.")
    private MakeDTO make;

    @NotEmpty(message = "Model is empty.")
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
}
