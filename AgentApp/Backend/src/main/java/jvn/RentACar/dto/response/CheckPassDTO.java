package jvn.RentACar.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class CheckPassDTO {

    @NotNull(message = "Found is null.")
    private Boolean found;
}
