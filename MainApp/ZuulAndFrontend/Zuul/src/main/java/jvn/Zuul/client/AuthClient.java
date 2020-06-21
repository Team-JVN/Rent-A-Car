package jvn.Zuul.client;

import jvn.Zuul.dto.UserDTO;
import jvn.Zuul.dto.UserSignedDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "users")
public interface AuthClient {

    @RequestMapping(method = RequestMethod.GET, path = "/api/verify")
    UserSignedDTO verify(@RequestHeader("Auth") String token);
}
