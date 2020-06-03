package jvn.Renting.dto.both;

import jvn.Renting.model.RentRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class MessageDTO {

    @Positive(message = "Id must be positive.")
    private Long id;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private Long sender;

    @Column(nullable = false)
    private String dateAndTime;

    @NotNull(message = "Rent request is null.")
    private RentRequestDTO rentRequest;
}
