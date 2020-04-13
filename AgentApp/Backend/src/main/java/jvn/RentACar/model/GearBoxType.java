package jvn.RentACar.model;

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
public class GearBoxType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String label;

    @OneToMany(mappedBy = "gearBoxType", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Car> cars = new HashSet<>();

}
