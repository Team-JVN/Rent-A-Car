package jvn.Zuul.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "users")
public interface AuthClient {

    @GetMapping("/verify")
    boolean verify();

}
