package jvn.Renting.dto.both;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class CarDTO {
    @NotNull(message = "Id is null.")
    @Positive(message = "Id must be positive.")
    private Long id;

    @NotBlank(message = "Name is empty.")
    @Pattern(regexp = "^(([A-Za-zÀ-ƒ]+[.]?[ ]?|[a-zÀ-ƒ]+['-]?){0,4})$", message = "Name is not valid.")
    private String make;

    @NotBlank(message = "Name is empty.")
    @Pattern(regexp = "^(([A-Za-zÀ-ƒ0-9]+[.]?[ ]?|[a-zÀ-ƒ0-9]+['-]?){0,4})$", message = "Name is not valid.")
    private String model;

    @NotBlank(message = "Name is empty.")
    @Pattern(regexp = "^(([A-Za-zÀ-ƒ]+[ ]?|[a-zÀ-ƒ]+['-]?){0,4})$", message = "Name is not valid.")
    private String fuelType;

    @NotBlank(message = "Name is empty.")
    @Pattern(regexp = "^(([A-Za-zÀ-ƒ0-9]+[ ]?|[a-zÀ-ƒ0-9]+['-]?){0,4})$", message = "Name is not valid.")
    private String gearBoxType;

    @NotBlank(message = "Name is empty.")
    @Pattern(regexp = "^(([A-Za-zÀ-ƒ]+[ ]?|[a-zÀ-ƒ]+['-]?){0,4})$", message = "Name is not valid.")
    private String bodyStyle;

    @NotNull(message = "Mileage is null.")
    @Min(value = 0, message = "Mileage must be positive number.")
    private Integer mileageInKm;

    @NotNull(message = "Number of kid's seats in km is null.")
    @Min(value = 0, message = "Number of kid's seats must be positive number")
    @Max(value = 3, message = "Max number of kid's seats is 3.")
    private Integer kidsSeats;

    @NotEmpty(message = "Pictures are null.")
    private Set<String> pictures = new HashSet<>();

    @NotNull(message = "Available tracking is null.")
    private Boolean availableTracking;

    @NotNull(message = "Id is null.")
    @Positive(message = "Id must be positive.")
    private Long owner;
}
