package jvn.Renting.client;

import jvn.Renting.dto.both.AdvertisementWithIdsDTO;
import jvn.Renting.dto.both.ClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "users")
public interface UserClient {

    @GetMapping("/api/client/verify/{clientId}")
    Boolean verify(@PathVariable("clientId") Long clientId);

    @GetMapping("/api/client/clients-by-id/{clientId}")
    List<ClientDTO> get(@PathVariable("clientId") List<Long> clients);
}
