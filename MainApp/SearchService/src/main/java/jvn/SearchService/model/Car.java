package jvn.SearchService.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jvn.SearchService.enumeration.LogicalStatus;
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
    private Long id;

    @Column(nullable = false)
    private LogicalStatus logicalStatus = LogicalStatus.EXISTING;

    @Column(nullable = false)
    private String make;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String fuelType;

    @Column(nullable = false)
    private String gearBoxType;

    @Column(nullable = false)
    private String bodyStyle;

    @Column(nullable = false)
    private Integer mileageInKm;

    @Column(nullable = false)
    private Integer kidsSeats;

    @ElementCollection
    private Set<String> pictures = new HashSet<>();

    @Column(nullable = false)
    private Boolean availableTracking;

    @Column(nullable = false)
    private Long owner;

    @JsonIgnore
    @OneToMany(mappedBy = "car", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Advertisement> advertisements = new HashSet<>();

    @Column
    private Double avgRating;

    @Column
    private Integer commentsCount;
}
