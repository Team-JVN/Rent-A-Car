package jvn.Zuul.client;

import jvn.Zuul.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "users")
public interface AuthClient {

    @GetMapping("/api/auth/verify{jwt-token}")
    UserDTO verify(@PathVariable("jwt-token") String jwtToken);
}
