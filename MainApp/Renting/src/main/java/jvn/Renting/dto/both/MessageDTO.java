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

    private String text;

    private Long sender;

    private String dateAndTime;

    @NotNull(message = "Rent request is null.")
    private RentRequestDTO rentRequest;
}
