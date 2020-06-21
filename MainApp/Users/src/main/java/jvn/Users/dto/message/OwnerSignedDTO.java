package jvn.Users.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OwnerSignedDTO {

    private byte[] ownerBytes;

    private byte[] digitalSignature;

}