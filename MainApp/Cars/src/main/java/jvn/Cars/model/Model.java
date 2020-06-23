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
public class Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    private Make make;

    @OneToMany(mappedBy = "model", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Car> cars = new HashSet<>();
}
