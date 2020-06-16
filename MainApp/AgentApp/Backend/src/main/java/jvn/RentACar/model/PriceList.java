package jvn.RentACar.model;

import jvn.RentACar.enumeration.LogicalStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PriceList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long mainAppId;

    @Column(nullable = false)
    private Double pricePerDay;

    @Column
    private Double pricePerKm;

    @Column
    private Double priceForCDW;

    @OneToMany(mappedBy = "priceList", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Advertisement> advertisements = new HashSet<>();

    @Column(nullable = false)
    private LogicalStatus status = LogicalStatus.EXISTING;

}
