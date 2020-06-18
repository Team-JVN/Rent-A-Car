package jvn.Advertisements.model;


import jvn.Advertisements.enumeration.LogicalStatus;
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

    @Column(nullable = false)
    private Double pricePerDay;

    @Column
    private Double pricePerKm;

    @Column
    private Double priceForCDW;

    @Column
    private Long ownerId;

    @OneToMany(mappedBy = "priceList", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Advertisement> advertisements = new HashSet<>();

    @Column(nullable = false)
    private LogicalStatus status = LogicalStatus.EXISTING;
}
