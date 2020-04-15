package jvn.RentACar.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class RentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String comment;

    @OneToOne(mappedBy = "rentReport", cascade = CascadeType.ALL)
    private RentRequest rentRequest;

    @Column(nullable = false)
    private Double additionalCost;

    @ElementCollection
    @CollectionTable(name = "advertisement_mileage_mapping",
            joinColumns = {@JoinColumn(name = "rent_report_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "advertisement_id")
    @Column(name = "km")
    private Map<Long, Integer> madeMileagePerAd;
}
