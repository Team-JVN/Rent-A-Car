package jvn.RentACar.dto.both;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class RentRequestDTO {
    @Positive(message = "Id must be positive.")
    private Long id;

    private ClientDTO client;

    @NotNull(message = "Set of rent info is null.")
    private Set<RentInfoDTO> rentInfos;

    @Positive(message = "Total price is not a positive number.")
    private Double totalPrice;

    @Pattern(regexp = "(?i)(Pending|Reserved|Paid|Canceled)?$",message = "Rent request status is not valid.")
    private String rentRequestStatus;
}
