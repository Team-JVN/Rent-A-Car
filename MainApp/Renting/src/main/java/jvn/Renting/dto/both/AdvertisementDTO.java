package jvn.Renting.dto.both;


import jvn.Renting.enumeration.LogicalStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
public class AdvertisementDTO {

    @NotNull(message = "Id is null.")
    @Positive(message = "Id must be positive.")
    private Long id;

    @NotNull(message = "Car is null.")
    private CarDTO car;

    @NotNull(message = "PriceList is null.")
    private PriceListDTO priceList;

    //    @NotBlank(message = "Date from is empty.")
//    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$", message = "Date from is not validly formatted")
    private String dateFrom;

    //    @Pattern(regexp = "^(\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01]))?$", message = "Date to is not validly formatted")
    private String dateTo;

    @Min(value = 0, message = "Kilometres limit must be positive number.")
    private Integer kilometresLimit;

    @Min(value = 0, message = "Discount must be a positive number.")
    @Max(value = 99, message = "Maximal discount is 99%.")
    private Integer discount;

    //    @NotNull(message = "CDW is null.")
    private Boolean CDW;

    @NotBlank(message = "Pick-up point  is empty.")
    @Pattern(regexp = "^([#.0-9a-zA-ZÀ-ƒ,-/]+[ ]?){1,10}$", message = "Pick up point is not valid.")
    private String pickUpPoint;

    //    @NotNull(message = "Owner is null.")
    private OwnerDTO owner;

    private LogicalStatus logicalStatus;
}
