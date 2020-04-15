package jvn.RentACar.dto.both;

import jvn.RentACar.model.GearboxType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class GearboxTypeDTO {
    @NotNull(message = "Id is null.")
    private Long id;

    @NotEmpty(message = "Name is empty.")
    private String name;

    public GearboxTypeDTO(GearboxType gearBoxType) {
        this.id = gearBoxType.getId();
        this.name = gearBoxType.getName();
    }
}
