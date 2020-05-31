package jvn.Advertisements.dto.message;

import jvn.Advertisements.dto.both.PriceListDTO;
import jvn.Advertisements.dto.response.CarWithAllInformationDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdvertisementMessageDTO {
    private Long id;

    private CarWithAllInformationDTO car;

    private PriceListDTO priceList;

    private Integer kilometresLimit;

    private Integer discount;

    private Boolean CDW;

    private String dateFrom;

    private String dateTo;

    private String pickUpPoint;

    private Long owner;

    private String ownerName;
}
