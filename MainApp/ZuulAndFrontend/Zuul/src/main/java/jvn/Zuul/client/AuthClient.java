package jvn.Zuul.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "cars")
public interface AuthClient {

    @GetMapping("/verify")
    boolean verify();

}
