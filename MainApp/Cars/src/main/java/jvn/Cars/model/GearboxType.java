package jvn.Cars.model;

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
public class GearboxType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "gearBoxType", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Car> cars = new HashSet<>();

    public GearboxType(String name) {
        this.name = name;
    }
}
