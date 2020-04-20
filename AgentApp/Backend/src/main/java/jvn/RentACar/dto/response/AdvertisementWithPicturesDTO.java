package jvn.RentACar.dto.response;

import jvn.RentACar.dto.both.AdvertisementDTO;
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
public class AdvertisementWithPicturesDTO {
    @NotNull(message = "Advertisement info is null.")
    private AdvertisementDTO advertisementDTO;

    @NotNull(message = "Pictures are null.")
    private List<String> pictures;

}
