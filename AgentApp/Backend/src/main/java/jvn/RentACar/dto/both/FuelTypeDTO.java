package jvn.RentACar.dto.both;

import jvn.RentACar.model.FuelType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class FuelTypeDTO {

    @NotNull(message = "Id is null.")
    private Long id;

    @NotEmpty(message = "Name is empty.")
    private String name;

    public FuelTypeDTO(FuelType fuelType) {
        this.id = fuelType.getId();
        this.name = fuelType.getName();
    }
}
