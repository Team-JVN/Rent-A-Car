package jvn.RentACar.dto.request;

import jvn.RentACar.dto.both.PriceListDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementEditDTO {

    @NotNull(message = "Price list is null.")
    private PriceListDTO priceList;

    @Min(1)
    private Integer kilometresLimit;

    @NotNull(message = "Discount is null.")
    @Min(0)
    @Max(99)
    private Integer discount;
}
