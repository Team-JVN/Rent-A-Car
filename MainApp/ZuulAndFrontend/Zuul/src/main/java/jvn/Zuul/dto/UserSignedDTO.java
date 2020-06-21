package jvn.Zuul.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignedDTO {

    private byte[] userBytes;

    private byte[] digitalSignature;

}
