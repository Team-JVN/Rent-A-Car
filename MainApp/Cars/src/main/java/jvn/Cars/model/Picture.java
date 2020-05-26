package jvn.Cars.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Picture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String data;

    //Edit will not working when you put CascadeType.ALL and FetchType.EAGER
    @ManyToOne
    private Car car;

    public Picture(String data, Car car) {
        this.data = data;
        this.car = car;
    }

}
