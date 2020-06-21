package rs.ac.uns.eureka.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogSignedDTO {

    private String sender;

    private String log;

    private byte[] digitalSignature;

}
