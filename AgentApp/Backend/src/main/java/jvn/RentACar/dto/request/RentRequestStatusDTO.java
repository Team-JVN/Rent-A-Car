package jvn.RentACar.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
public class RentRequestStatusDTO {

    @NotBlank(message = "Status is empty.")
    @Pattern(regexp = "(?i)(Pending|Reserved|Paid|Canceled)$",message = "Rent request status is not valid.")
    private String status;
}
