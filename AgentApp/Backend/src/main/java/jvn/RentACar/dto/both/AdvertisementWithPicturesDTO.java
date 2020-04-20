package jvn.RentACar.dto.both;

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
public class AdvertisementWithPicturesDTO {
    @NotNull(message = "Id is null.")
    private Long id;

    @NotNull(message = "Car is null.")
    private CarWithPicturesDTO carWithPicturesDTO;

    @NotNull(message = "Price list is null.")
    private PriceListDTO priceList;

    @NotNull(message = "Kilometres limit in km is null.")
    @Min(1)
    private Integer kilometresLimit;

    @NotNull(message = "Discount is null.")
    @Min(0)
    @Max(99)
    private Integer discount;

    @NotNull(message = "CDW is null.")
    private Boolean CDW;

    @NotEmpty(message = "Date from is empty.")
    private String dateFrom;
}
