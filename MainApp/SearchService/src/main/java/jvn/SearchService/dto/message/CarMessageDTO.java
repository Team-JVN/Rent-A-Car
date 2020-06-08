package jvn.SearchService.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarMessageDTO {

    private Long id;

    private Integer mileageInKm;

    private Integer kidsSeats;

    private Boolean availableTracking;
}
