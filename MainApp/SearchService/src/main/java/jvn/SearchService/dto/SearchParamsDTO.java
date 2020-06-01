package jvn.SearchService.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
public class SearchParamsDTO {

    @NotBlank(message = "Date and time from cannot be empty.")
    @Pattern(regexp = "^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]$", message = "Date and time from are not validly formatted.")
    private String dateTimeFrom;

    @NotBlank(message = "Date and time to cannot be empty.")
    @Pattern(regexp = "^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]$", message = "Date and time from are not validly formatted.")
    private String dateTimeTo;

    @NotBlank(message = "Pick-up point cannot be empty.")
    @Pattern(regexp = "^([#.0-9a-zA-ZÀ-ƒ,-/]+[ ]?){1,10}$", message = "Pick-up point is not valid.")
    private String pickUpPoint;

    @Pattern(regexp = "^(([A-Za-zÀ-ƒ]+[.]?[ ]?|[a-zÀ-ƒ]+['-]?){0,4})$", message = "Make is not valid.")
    private String make;

    @Pattern(regexp = "^(([A-Za-zÀ-ƒ0-9]+[.]?[ ]?|[a-zÀ-ƒ0-9]+['-]?){0,4})$", message = "Model is not valid.")
    private String model;

    @Pattern(regexp = "^(([A-Za-zÀ-ƒ]+[.]?[ ]?|[a-zÀ-ƒ]+['-]?){0,4})$", message = "Fuel type is not valid.")
    private String fuelType;

    @Pattern(regexp = "^(([A-Za-zÀ-ƒ0-9]+[.]?[ ]?|[a-zÀ-ƒ0-9]+['-]?){0,4})$", message = "Gearbox type is not valid.")
    private String gearBoxType;

    @Pattern(regexp = "^(([A-Za-zÀ-ƒ]+[.]?[ ]?|[a-zÀ-ƒ]+['-]?){0,4})$", message = "Body style is not valid.")
    private String bodyStyle;

    @PositiveOrZero(message = "Minimal rating must be a positive number.")
    private Integer minRating;

    @PositiveOrZero(message = "Minimal price per day must be a positive number.")
    private Double minPricePerDay;

    @PositiveOrZero(message = "Maximal price per day must be a positive number.")
    private Double maxPricePerDay;

    @Min(value = 0, message = "Number of kid's seats must be a positive number")
    @Max(value = 3, message = "Number of kid's seats can be 3 at most.")
    private Integer kidsSeats;

    @PositiveOrZero(message = "Maximal mileage must be a positive number.")
    private Integer mileageInKm;

    @PositiveOrZero(message = "Kilometres limit must be a positive number.")
    private Integer kilometresLimit;

    private Boolean CDW;
}