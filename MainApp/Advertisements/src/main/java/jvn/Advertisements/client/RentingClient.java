package jvn.Advertisements.client;

import jvn.Advertisements.enumeration.EditType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "renting")
public interface RentingClient {

    @RequestMapping(method = RequestMethod.GET, path = "/api/rent-request/advertisement/{advId}/check-for-delete")
    boolean canDeleteAdvertisement(@RequestHeader("Auth") String token, @RequestHeader("user") String user, @PathVariable("advId") Long advId);

    @RequestMapping(method = RequestMethod.GET, path = "/api/rent-request/advertisement/{advId}/edit-type")
    EditType getAdvertisementEditType(@RequestHeader("Auth") String token, @RequestHeader("user") String user, @PathVariable("advId") Long advId);
}
