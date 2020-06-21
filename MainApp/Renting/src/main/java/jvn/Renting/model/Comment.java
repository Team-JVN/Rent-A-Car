package jvn.Renting.model;

import jvn.Renting.enumeration.CommentStatus;
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

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false)
    private String senderName;

    @Column(nullable = false)
    private CommentStatus status;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private RentInfo rentInfo;
}
