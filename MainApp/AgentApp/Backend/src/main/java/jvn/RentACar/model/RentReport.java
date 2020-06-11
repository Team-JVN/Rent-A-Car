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

//    @OneToOne(mappedBy = "rentReport", cascade = CascadeType.ALL)
    @OneToOne
    private RentInfo rentInfo;

    @Column
    private Double additionalCost;

    @Column
    private Integer madeMileage;

    @Column
    private Boolean paid;
}
