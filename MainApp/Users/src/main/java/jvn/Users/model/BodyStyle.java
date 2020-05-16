package jvn.Users.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class BodyStyle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    public BodyStyle(String name) {
        this.name = name;
    }
}
