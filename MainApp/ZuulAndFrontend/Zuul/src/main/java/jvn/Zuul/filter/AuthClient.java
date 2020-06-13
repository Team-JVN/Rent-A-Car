package jvn.Zuul.filter;

import jvn.Zuul.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "users")
public interface AuthClient {

    @RequestMapping(method = RequestMethod.GET, path = "/api/verify")
    UserDTO verify(@RequestHeader("Auth") String token);

}
