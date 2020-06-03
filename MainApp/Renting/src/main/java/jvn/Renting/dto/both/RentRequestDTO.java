package jvn.Renting.dto.both;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class RentRequestDTO {
    @Positive(message = "Id must be positive.")
    private Long id;

    private ClientDTO client;

    @NotEmpty(message = "Set of rent infos is empty.")
    private Set<RentInfoDTO> rentInfos;

    @Positive(message = "Total price is not a positive number.")
    private Double totalPrice;

    //    @Pattern(regexp = "(?i)(Pending|Reserved|Paid|Canceled|undefined)?$", message = "Rent request status is not valid.")
    private String rentRequestStatus;
}
