package jvn.SearchService.dto;

import jvn.SearchService.enumeration.LogicalStatus;
import jvn.SearchService.model.Car;
import jvn.SearchService.model.Owner;
import jvn.SearchService.model.PriceList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdvertisementDTO {

    private Long id;

    private LogicalStatus logicalStatus;

    private Car car;

    private PriceList priceList;

    private String dateFrom;

    private String dateTo;

    private Integer kilometresLimit;

    private Integer discount;

    private Boolean CDW;

    private String pickUpPoint;

    private Owner owner;
}
