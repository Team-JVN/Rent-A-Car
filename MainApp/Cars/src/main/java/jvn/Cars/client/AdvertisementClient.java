package jvn.Cars.client;

import jvn.Cars.enumeration.EditType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "advertisements")
public interface AdvertisementClient {

    @RequestMapping(method = RequestMethod.GET, path = "/api/advertisement/car/{carId}/check-for-delete")
    boolean canDeleteCar(@RequestHeader("Auth") String token, @RequestHeader("user") String user, @PathVariable("carId") Long carId);

    @RequestMapping(method = RequestMethod.GET, path = "/api/advertisement/car/{carId}/check-for-partial-edit")
    boolean canEditCarPartially(@RequestHeader("Auth") String token, @RequestHeader("user") String user, @PathVariable("carId") Long carId);

    @RequestMapping(method = RequestMethod.GET, path = "/api/advertisement/car/{carId}/edit-type")
    EditType getCarEditType( @PathVariable("carId") Long carId);
}
