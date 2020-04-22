package jvn.RentACar.dto.request;

import jvn.RentACar.dto.both.CarDTO;
import jvn.RentACar.dto.both.PriceListDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class CreateAdvertisementDTO {
    @NotNull(message = "Car is null.")
    private CarDTO car;

    @NotNull(message = "Price list is null.")
    private PriceListDTO priceList;

    @Min(1)
    private Integer kilometresLimit;

    @Min(0)
    @Max(99)
    private Integer discount;

    @NotEmpty(message = "Date from is empty.")
    private String dateFrom;

}
