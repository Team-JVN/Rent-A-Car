package jvn.Renting.client;

import jvn.Renting.dto.both.AdvertisementDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "search-service")
public interface SearchClient {

    @GetMapping("/api/advertisement/for-rent-requests/{advId}")
    List<AdvertisementDTO> get(@PathVariable("advId") List<Long> advertisements);
}
