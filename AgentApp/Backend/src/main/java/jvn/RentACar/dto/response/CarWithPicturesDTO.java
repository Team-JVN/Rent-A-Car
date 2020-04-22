package jvn.RentACar.dto.response;

import jvn.RentACar.dto.both.CarDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarWithPicturesDTO {
    @NotNull(message = "Car info is null.")
    private CarDTO carDTO;

    @NotNull(message = "Pictures are null.")
    private List<String> pictures;
}
