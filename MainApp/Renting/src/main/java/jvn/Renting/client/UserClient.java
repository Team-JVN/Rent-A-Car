package jvn.Renting.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "users")
public interface UserClient {

    @GetMapping("/api/client/verify/{clientId}")
    Boolean verify(@PathVariable("clientId") Long clientId);
}
