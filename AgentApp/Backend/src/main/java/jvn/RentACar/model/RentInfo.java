package jvn.RentACar.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class RentInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateTimeFrom;

    @Column(nullable = false)
    private LocalDateTime dateTimeTo;

    @Column(nullable = false)
    private String location;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Advertisement advertisement;

    @Column(nullable = false)
    private Boolean optedForCDWMap;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private RentRequest rentRequest;
}
