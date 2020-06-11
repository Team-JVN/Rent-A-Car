package jvn.SearchService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementEditDTO {
    @Positive(message = "Id must be positive.")
    private Long id;

    @NotNull(message = "Price list is null.")
    private PriceListDTO priceList;

    @Min(value = 0, message = "Kilometres limit must be positive number.")
    private Integer kilometresLimit;

    @Min(value = 0, message = "Discount must be a positive number.")
    @Max(value = 99, message = "Maximal discount is 99%.")
    private Integer discount;
}