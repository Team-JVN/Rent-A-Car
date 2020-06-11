package jvn.Renting.dto.both;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class FeedbackDTO {

    @NotNull(message = "Rating is null.")
    @Min(value = 0, message = "Rating must be positive number.")
    @Max(value = 5, message = "Max rating is 5.")
    private Integer rating;

    private Set<CommentDTO> comments;

}
