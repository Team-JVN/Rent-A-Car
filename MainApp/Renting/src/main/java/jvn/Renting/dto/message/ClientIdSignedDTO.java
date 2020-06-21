package jvn.Renting.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientIdSignedDTO {

    private byte[] clientIdBytes;

    private byte[] digitalSignature;

}
