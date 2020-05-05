package jvn.RentACar.dto.both;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class RentRequestDTO {

    private Long id;

    private ClientDTO client;

    @NotNull(message = "Set of rent info is null.")
    private Set<RentInfoDTO> rentInfos;

    private Double totalPrice;

    private String rentRequestStatus;
}
