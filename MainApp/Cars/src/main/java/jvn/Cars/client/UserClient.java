package jvn.Cars.client;

import jvn.Cars.dto.response.UserInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "users")
public interface UserClient {

    @RequestMapping(method = RequestMethod.GET, path = "/api/verify/user")
    UserInfoDTO getUser(@RequestParam("email") String email);
}