package jvn.RentACar.dto.both;


import jvn.RentACar.model.BodyStyle;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class BodyStyleDTO {

    @NotNull(message = "Id is null.")
    private Long id;

    @NotEmpty(message = "Name is empty.")
    private String name;

    public BodyStyleDTO(BodyStyle bodyStyle) {
        this.id = bodyStyle.getId();
        this.name = bodyStyle.getName();
    }
}
