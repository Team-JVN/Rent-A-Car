package jvn.Users.model;

import jvn.Users.enumeration.AdminStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    @Enumerated(EnumType.STRING)
    private AdminStatus status;

    public Admin(String name, String email, String password, Role role) {
        super(name, email, password, role);
        this.status = AdminStatus.INACTIVE;
    }
}