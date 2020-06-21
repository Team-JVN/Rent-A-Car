package jvn.Zuul.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignedMessageDTO {

    private byte[] messageBytes;

    private byte[] digitalSignature;

}