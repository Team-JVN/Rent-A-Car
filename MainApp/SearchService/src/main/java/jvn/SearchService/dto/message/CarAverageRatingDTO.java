package jvn.SearchService.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarAverageRatingDTO {

    @NotNull(message = "Car is null")
    @Positive(message = "Car's id must be positive.")
    private Long carId;

    @NotNull(message = "Rating is null")
    @Positive(message = "Rating must be positive number.")
    private Double rating;
}
