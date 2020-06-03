package jvn.Renting.client;

import jvn.Renting.dto.both.ClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "users")
public interface UserClient {

    @RequestMapping(method = RequestMethod.GET, path = "/api/client/verify/{clientId}")
    Boolean verify(@RequestHeader("Auth") String token,
                   @PathVariable("clientId") Long clientId);

    @RequestMapping(method = RequestMethod.GET, path = "/api/client/clients-by-id/{clientId}")
    List<ClientDTO> get(@RequestHeader("Auth") String token,
                        @PathVariable("clientId") List<Long> clients);
}
