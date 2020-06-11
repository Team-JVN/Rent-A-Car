package jvn.Users.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentRequestMessageDTO {
    @NotNull(message = "Client is null")
    @Positive(message = "Client's id must be positive.")
    private Long clientId;

    @NotNull(message = "Rent request is null")
    @Positive(message = "Rent request's id must be positive.")
    private Long rentRequestId;
}

