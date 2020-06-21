package jvn.Advertisements.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementSignedDTO {

    private byte[] advertisementBytes;

    private byte[] digitalSignature;

}
