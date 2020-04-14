package jvn.RentACar.model;

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

    @OneToMany(mappedBy = "priceList", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Advertisement> advertisements = new HashSet<>();

    public PriceList(Double pricePerDay, Double pricePerKm, Double priceForCDW) {
        this.pricePerDay = pricePerDay;
        this.pricePerKm = pricePerKm;
        this.priceForCDW = priceForCDW;
    }

}
