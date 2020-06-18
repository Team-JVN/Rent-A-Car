package jvn.Renting.client;

import jvn.Renting.dto.both.ClientDTO;
import jvn.Renting.dto.response.UserInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "users")
public interface UserClient {

    @RequestMapping(method = RequestMethod.GET, path = "/api/client/{clientId}/verify")
    Boolean verify(@PathVariable("clientId") Long clientId);


    @RequestMapping(method = RequestMethod.GET, path = "/api/client/by-ids/{clientIds}")
    List<ClientDTO> get(@RequestHeader("Auth") String token,
                        @PathVariable("clientIds") List<Long> clients);

    @RequestMapping(method = RequestMethod.GET, path = "/api/verify/user")
    UserInfoDTO getUser(@RequestParam("email") String email);
}
