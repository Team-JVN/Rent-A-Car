package jvn.SearchService.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementEditSignedDTO {

    private byte[] advertisementEditBytes;

    private byte[] digitalSignature;

}
