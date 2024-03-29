package jvn.Renting.client;

import jvn.Renting.dto.both.AdvertisementWithIdsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "advertisements")
public interface AdvertisementClient {

    @RequestMapping(method = RequestMethod.GET, path = "/api/advertisement/by-ids/{advIds}")
    List<AdvertisementWithIdsDTO> get(@PathVariable("advIds") List<Long> advertisements);


    @RequestMapping(method = RequestMethod.GET, path = "/api/advertisement/by-id/{advId}")
    AdvertisementWithIdsDTO getOne(@PathVariable("advId") Long advertisement);

}
