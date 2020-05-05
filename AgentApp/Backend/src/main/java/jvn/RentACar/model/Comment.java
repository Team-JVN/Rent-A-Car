package jvn.RentACar.model;

import jvn.RentACar.enumeration.CommentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private User sender;

    @Column(nullable = false)
    private CommentStatus status;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private RentInfo rentInfo;
}
