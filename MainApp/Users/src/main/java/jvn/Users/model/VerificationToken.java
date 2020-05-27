package jvn.Users.model;

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
public class VerificationToken {

    private static final int VERIFICATION_TOKEN_EXPIRY_TIME = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @Column(nullable = false)
    private LocalDateTime expiryDateTime;

    @OneToOne(targetEntity = Client.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "client_id")
    private Client client;

    public VerificationToken(Client client) {
        this.token = UUID.randomUUID().toString();
        this.client = client;
        this.createdDateTime = LocalDateTime.now();
        this.expiryDateTime = createdDateTime.plusHours(VERIFICATION_TOKEN_EXPIRY_TIME);
    }
}

