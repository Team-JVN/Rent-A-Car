package jvn.Renting.dto.both;

import jvn.Renting.enumeration.CommentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
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

    @NotNull(message = "Rent info is null.")
    private RentInfoDTO rentInfo;

}
