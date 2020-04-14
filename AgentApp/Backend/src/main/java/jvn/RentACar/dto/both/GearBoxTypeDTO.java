package jvn.RentACar.dto.both;

import jvn.RentACar.model.GearBoxType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class GearBoxTypeDTO {
    @NotNull(message = "Id is null.")
    private Long id;

    @NotEmpty(message = "Name is empty.")
    private String name;

    public GearBoxTypeDTO(GearBoxType gearBoxType) {
        this.id = gearBoxType.getId();
        this.name = gearBoxType.getName();
    }
}
