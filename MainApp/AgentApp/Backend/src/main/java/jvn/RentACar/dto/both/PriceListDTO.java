package jvn.RentACar.dto.both;

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
public class PriceListDTO {

    @Positive(message = "Id must be positive.")
    private Long id;

    @NotNull(message = "Price per day is null.")
    @Positive(message = "Price per day is not a positive number.")
    private Double pricePerDay;

    @Positive(message = "Price per kilometer is not a positive number.")
    private Double pricePerKm;

    @Positive(message = "Price for CDW is not a positive number.")
    private Double priceForCDW;

}
