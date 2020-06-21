package jvn.SearchService.dto.message;

import jvn.SearchService.model.Advertisement;
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
