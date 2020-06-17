package jvn.RentACar.dto.both;

import jvn.RentACar.enumeration.CommentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
public class CommentDTO {

    @Positive(message = "Id must be positive.")
    private Long id;

    private String text;

    private UserDTO sender;

    private CommentStatus status;

//    private RentInfoDTO rentInfo;

}
