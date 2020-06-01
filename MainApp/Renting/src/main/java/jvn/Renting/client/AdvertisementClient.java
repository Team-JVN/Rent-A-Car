package jvn.Renting.client;

import jvn.Renting.dto.both.AdvertisementWithIdsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "advertisements")
public interface AdvertisementClient {

    @GetMapping("/api/advertisement/{advId}")
    List<AdvertisementWithIdsDTO> get(@PathVariable("advId") List<Long> advertisements);
}
