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
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LogicalStatus logicalStatus = LogicalStatus.EXISTING;

    @Column(nullable = false)
    private String make;

    @Column(nullable = false)
    private String model;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private FuelType fuelType;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private GearboxType gearBoxType;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private BodyStyle bodyStyle;

    @Column(nullable = false)
    private Integer mileageInKm;

    @Column(nullable = false)
    private Integer kidsSeats;

    //Edit will not working when you put CascadeType.ALL and FetchType.EAGER
    @OneToMany(mappedBy = "car")
    private Set<Picture> pictures = new HashSet<>();

    @Column(nullable = false)
    private Boolean availableTracking;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private User owner;

    @OneToMany(mappedBy = "car", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Advertisement> advertisements = new HashSet<>();

}
