package jvn.RentACar.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.HashSet;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("AGENT")
public class Agent extends User {

    @Column
    private String taxIdNumber;

    public Agent(String name, String email, String password, String address, String taxIdNumber) {
        super(null, name, email, password, address, true, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
        this.taxIdNumber = taxIdNumber;
    }
}
