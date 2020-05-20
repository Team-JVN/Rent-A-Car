package jvn.RentACar.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ResetToken {

    private static final int RESET_TOKEN_EXPIRY_TIME = 45;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @Column(nullable = false)
    private LocalDateTime expiryDateTime;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    public ResetToken(User user) {
        this.token = UUID.randomUUID().toString();
        this.user = user;
        this.createdDateTime = LocalDateTime.now();
        this.expiryDateTime = createdDateTime.plusMinutes(RESET_TOKEN_EXPIRY_TIME);
    }
}
