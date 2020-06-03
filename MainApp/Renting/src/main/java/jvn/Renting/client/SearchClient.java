package jvn.Renting.client;

import jvn.Renting.dto.both.AdvertisementDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "search-service")
public interface SearchClient {

    @RequestMapping(method = RequestMethod.GET, path = "/api/advertisement/for-rent-requests/{advId}")
    List<AdvertisementDTO> get(@RequestHeader("Auth") String token, @RequestHeader("user") String user,
                               @PathVariable("advId") List<Long> advertisements);
}

