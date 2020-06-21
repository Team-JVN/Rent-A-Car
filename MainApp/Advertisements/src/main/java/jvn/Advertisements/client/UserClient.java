package jvn.Advertisements.client;

import jvn.Advertisements.dto.response.SignedMessageDTO;
import jvn.Advertisements.dto.response.UserInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "users")
public interface UserClient {

    @RequestMapping(method = RequestMethod.GET, path = "/api/verify/user")
    SignedMessageDTO getUser(@RequestParam("email") String email);
}
