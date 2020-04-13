package jvn.RentACar.model;

import jvn.RentACar.enumeration.RentRequestStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class RentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RentRequestStatus rentRequestStatus;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Client client;

    @ManyToMany(mappedBy = "rentRequests", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Advertisement> advertisements = new HashSet<>();

    @Column(nullable = false)
    private Double totalPrice;


    @ElementCollection
    @CollectionTable(name = "advertisement_cdw_mapping",
            joinColumns = {@JoinColumn(name = "rent_request_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "advertisement_id")
    @Column(name = "cdw")
    private Map<Long, Boolean> optedForCDWMap;

    @OneToOne
    private RentReport rentReport;
}
