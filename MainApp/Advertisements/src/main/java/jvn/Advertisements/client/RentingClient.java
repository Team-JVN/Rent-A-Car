package jvn.Advertisements.client;

import jvn.Advertisements.dto.response.SignedMessageDTO;
import jvn.Advertisements.enumeration.EditType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "renting")
public interface RentingClient {

    @RequestMapping(method = RequestMethod.GET, path = "/api/rent-request/advertisement/{advId}/check-for-delete")
    SignedMessageDTO canDeleteAdvertisement(@PathVariable("advId") Long advId);

    @RequestMapping(method = RequestMethod.GET, path = "/api/rent-request/advertisement/{advIds}/check-rent-infos")
    SignedMessageDTO hasRentInfos(@PathVariable("advIds") List<Long> advIds);

    @RequestMapping(method = RequestMethod.GET, path = "/api/rent-request/advertisement/{advId}/edit-type-feign")
    SignedMessageDTO getAdvertisementEditTypeFeign(@PathVariable("advId") Long advId);
}
