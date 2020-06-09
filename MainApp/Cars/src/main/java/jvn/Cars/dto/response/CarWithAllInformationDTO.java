package jvn.Cars.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarWithAllInformationDTO {

    @Positive(message = "Id must be positive.")
    @NotNull(message = "Id is null.")
    private Long id;

    @NotBlank(message = "Logical status is empty.")
    private String logicalStatus;

    @NotBlank(message = "Make is empty.")
    @Pattern(regexp = "^(([A-Za-zÀ-ƒ]+[.]?[ ]?|[a-zÀ-ƒ]+['-]?){0,4})$", message = "Name is not valid.")
    private String make;

    @NotBlank(message = "Model is empty.")
    @Pattern(regexp = "^(([A-Za-zÀ-ƒ0-9]+[.]?[ ]?|[a-zÀ-ƒ0-9]+['-]?){0,4})$", message = "Name is not valid.")
    private String model;

    @NotBlank(message = "Fuel type is empty.")
    @Pattern(regexp = "^(([A-Za-zÀ-ƒ]+[ ]?|[a-zÀ-ƒ]+['-]?){0,4})$", message = "Name is not valid.")
    private String fuelType;

    @NotBlank(message = "Gearbox type is empty.")
    @Pattern(regexp = "^(([A-Za-zÀ-ƒ0-9]+[ ]?|[a-zÀ-ƒ0-9]+['-]?){0,4})$", message = "Name is not valid.")
    private String gearBoxType;

    @NotBlank(message = "Body style is empty.")
    @Pattern(regexp = "^(([A-Za-zÀ-ƒ]+[ ]?|[a-zÀ-ƒ]+['-]?){0,4})$", message = "Name is not valid.")
    private String bodyStyle;

    @NotNull(message = "Mileage is null.")
    @Min(value = 0, message = "Mileage must be positive number.")
    private Integer mileageInKm;

    @NotNull(message = "Number of kid's seats in km is null.")
    @Min(value = 0, message = "Number of kid's seats must be positive number")
    @Max(value = 3, message = "Max number of kid's seats is 3.")
    private Integer kidsSeats;

    @NotNull(message = "Available tracking is null.")
    private Boolean availableTracking;

    @NotNull(message = "Average rating is null.")
    @PositiveOrZero(message = "Average rating is not a positive number.")
    private Double avgRating;

    @NotNull(message = "Comment count is null.")
    @PositiveOrZero(message = "Comment count is not a positive number.")
    private Integer commentsCount;

    @NotEmpty(message = "Pictures are null.")
    private List<String> pictures;

    @NotNull(message = "Owner is null")
    @Positive(message = "Id must be positive.")
    private Long owner;
}
