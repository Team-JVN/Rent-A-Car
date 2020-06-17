package jvn.Renting.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false)
    private String senderEmail;

    @Column(nullable = false)
    private LocalDateTime dateAndTime;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private RentRequest rentRequest;
    
}
