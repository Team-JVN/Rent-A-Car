package jvn.Renting.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jvn.Renting.enumeration.RentRequestStatus;
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
public class RentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RentRequestStatus rentRequestStatus;

    @Column(nullable = false)
    private Long client;

    @Column(nullable = false)
    private Double totalPrice;

    @OneToMany(mappedBy = "rentRequest", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<RentInfo> rentInfos = new HashSet<RentInfo>();

    @OneToMany(mappedBy = "rentRequest", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Message> messages = new HashSet<>();

    @Column(nullable = false)
    private Long createdBy;

    @Column(nullable = false)
    private Long advertisementOwner;
}
