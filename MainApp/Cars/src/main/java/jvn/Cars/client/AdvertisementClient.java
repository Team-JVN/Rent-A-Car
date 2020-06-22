package jvn.Cars.client;

import jvn.Cars.dto.response.SignedMessageDTO;
import jvn.Cars.enumeration.EditType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "advertisements")
public interface AdvertisementClient {

    @RequestMapping(method = RequestMethod.GET, path = "/api/advertisement/car/{carId}/check-for-delete")
    SignedMessageDTO canDeleteCar(@PathVariable("carId") Long carId);

    @RequestMapping(method = RequestMethod.GET, path = "/api/advertisement/car/{carId}/check-for-partial-edit")
    SignedMessageDTO canEditCarPartially(@PathVariable("carId") Long carId);

    @RequestMapping(method = RequestMethod.GET, path = "/api/advertisement/car/{carId}/edit-type-feign")
    SignedMessageDTO getCarEditTypeFeign(@PathVariable("carId") Long carId);
}
