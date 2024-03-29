package jvn.RentACar.model;

import jvn.RentACar.enumeration.RentRequestStatus;
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

    @Column(unique = true)
    private Long mainAppId;

    @Enumerated(EnumType.STRING)
    private RentRequestStatus rentRequestStatus;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Client client;

    @Column(nullable = false)
    private Double totalPrice;

    @OneToMany(mappedBy = "rentRequest", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<RentInfo> rentInfos = new HashSet<RentInfo>();

    @OneToMany(mappedBy = "rentRequest", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Message> messages = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private User createdBy;
}
