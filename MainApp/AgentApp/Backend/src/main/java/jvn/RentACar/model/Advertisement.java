package jvn.RentACar.model;

import jvn.RentACar.enumeration.LogicalStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long mainAppId;

    @Column(nullable = false)
    private LogicalStatus logicalStatus = LogicalStatus.EXISTING;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Car car;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
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

    @OneToMany(mappedBy = "advertisement", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<RentInfo> rentInfos = new HashSet<>();
}
