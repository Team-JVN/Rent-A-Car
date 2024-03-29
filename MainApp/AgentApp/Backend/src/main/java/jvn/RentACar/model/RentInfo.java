package jvn.RentACar.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class RentInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long mainAppId;

    @Column(nullable = false)
    private LocalDateTime dateTimeFrom;

    @Column(nullable = false)
    private LocalDateTime dateTimeTo;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Advertisement advertisement;

    @Column
    private Boolean optedForCDW;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private RentRequest rentRequest;

    @OneToOne(mappedBy = "rentInfo")
    private RentReport rentReport;

    @Column
    private Integer rating;

    @OneToMany(mappedBy = "rentInfo", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Comment> comments = new HashSet<>();

    @Column
    private Integer kilometresLimit;

    @Column
    private Double pricePerKm;


}
