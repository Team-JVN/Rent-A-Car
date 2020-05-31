package jvn.Users.model;

import jvn.Users.enumeration.AgentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("AGENT")
public class Agent extends User {

    @Column
    private String address;

    @Column(columnDefinition = "VARCHAR(10)", unique = true)
    private String phoneNumber;

    @Column
    private String taxIdNumber;

    @Enumerated(EnumType.STRING)
    private AgentStatus status;

    public Agent(String name, String email, String password, Role role, String address, String phoneNumber, String taxIdNumber) {
        super(name, email, password, role);
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.taxIdNumber = taxIdNumber;
        this.status = AgentStatus.INACTIVE;
    }

}
