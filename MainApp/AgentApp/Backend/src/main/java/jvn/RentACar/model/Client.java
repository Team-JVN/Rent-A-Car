package jvn.RentACar.model;

import jvn.RentACar.enumeration.ClientStatus;
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
@DiscriminatorValue("CLIENT")
public class Client extends User {

    @Column(columnDefinition = "VARCHAR(10)", unique = true)
    private String phoneNumber;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<RentRequest> clientRentRequests = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private ClientStatus status;

}
