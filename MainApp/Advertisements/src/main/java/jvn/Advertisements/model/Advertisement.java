package jvn.Advertisements.model;

import jvn.Advertisements.enumeration.LogicalStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LogicalStatus logicalStatus = LogicalStatus.EXISTING;

    @Column(nullable = false)
    private Long car;

    @ManyToOne(fetch = FetchType.EAGER)
    private PriceList priceList;

    @Column(nullable = false)
    private LocalDate dateFrom;

    @Column
    private LocalDate dateTo;

    @Column
    private Integer kilometresLimit;

    @Column
    private Integer discount;

    @Column
    private Boolean CDW;

    @Column
    private String pickUpPoint;

    @Column(nullable = false)
    private Long owner;

}
