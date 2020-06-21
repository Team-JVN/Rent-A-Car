package jvn.Zuul.client;

import jvn.Zuul.dto.SignedMessageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "users")
public interface AuthClient {

    @RequestMapping(method = RequestMethod.GET, path = "/api/verify")
    SignedMessageDTO verify(@RequestHeader("Auth") String token);
}
