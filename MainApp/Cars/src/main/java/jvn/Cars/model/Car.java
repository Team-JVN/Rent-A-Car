package jvn.Cars.model;

import jvn.Cars.enumeration.LogicalStatus;
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

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Make make;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Model model;

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

    //Edit is not working when you put CascadeType.ALL and FetchType.EAGER
    @OneToMany(mappedBy = "car", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Picture> pictures = new HashSet<>();

    @Column(nullable = false)
    private Boolean availableTracking;

    @Column(nullable = false)
    private Long owner;

    @Column
    private Double avgRating;

    @Column
    private Integer sumRating = 0;

    @Column
    private Integer countRating = 0;

    @Column
    private Integer commentsCount;

}
