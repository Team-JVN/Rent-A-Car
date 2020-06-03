package jvn.Advertisements.client;

import jvn.Advertisements.dto.response.CarWithAllInformationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "cars")
public interface CarClient {

    @RequestMapping(method = RequestMethod.GET, path = "/api/car/verify/{userId}/{carId}")
    CarWithAllInformationDTO verify(@RequestHeader("Auth") String token, @RequestHeader("user") String user,
                                    @PathVariable("userId") Long userId, @PathVariable("carId") Long carId);
}
