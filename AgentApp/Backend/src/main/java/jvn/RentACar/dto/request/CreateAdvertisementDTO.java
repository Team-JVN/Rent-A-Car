package jvn.RentACar.dto.request;

import jvn.RentACar.dto.both.PriceListDTO;
import jvn.RentACar.dto.response.CarWithPicturesDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
public class CreateAdvertisementDTO {
    @NotNull(message = "Car is null.")
    private CarWithPicturesDTO car;

    @NotNull(message = "Price list is null.")
    private PriceListDTO priceList;

    @Min(value = 0,message = "Kilometres limit must be positive number.")
    private Integer kilometresLimit;

    @Min(value = 0,message = "Discount must be a positive number.")
    @Max(value = 99,message = "Maximal discount is 99%.")
    private Integer discount;

    @NotBlank(message = "Date from is empty.")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$",message = "Date from is not validly formatted")
    private String dateFrom;

    @Pattern(regexp = "^(\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01]))?$",message = "Date to is not validly formatted")
    private String dateTo;

    @NotBlank(message = "Pick-up point  is empty.")
    @Pattern(regexp = "^([#.0-9a-zA-ZÀ-ƒ,-/]+[ ]?){1,10}$",message = "Pick up point is not valid.")
    private String pickUpPoint;
}
