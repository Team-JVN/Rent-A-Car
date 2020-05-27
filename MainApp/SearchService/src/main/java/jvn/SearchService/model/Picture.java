package jvn.SearchService.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Picture {

    @Id
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
