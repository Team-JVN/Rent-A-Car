package jvn.Users.model;

import jvn.Users.enumeration.ClientStatus;
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

    @Column
    private String address;

    @Column(columnDefinition = "VARCHAR(10)", unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private ClientStatus status;

    @Column
    private Integer canceledReservationCounter;

    @Column
    private Integer rejectedCommentsCounter;
}
