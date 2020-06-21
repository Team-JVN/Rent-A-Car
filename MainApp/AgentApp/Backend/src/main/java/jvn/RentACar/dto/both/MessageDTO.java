package jvn.RentACar.dto.both;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
public class MessageDTO {

    @Positive(message = "Id must be positive.")
    private Long id;

    private String text;

    private UserDTO sender;

    private String dateAndTime;

//    private RentRequestDTO rentRequest;
}
