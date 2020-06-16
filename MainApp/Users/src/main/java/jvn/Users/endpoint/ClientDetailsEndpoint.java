package jvn.Users.endpoint;

import jvn.Users.dto.response.UserInfoDTO;
import jvn.Users.dto.soap.client.ClientDetails;
import jvn.Users.dto.soap.client.CreateOrEditClientRequest;
import jvn.Users.dto.soap.client.CreateOrEditClientResponse;
import jvn.Users.mapper.ClientDetailsMapper;
import jvn.Users.model.Client;
import jvn.Users.service.ClientService;
import jvn.Users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.security.NoSuchAlgorithmException;

@Endpoint
public class ClientDetailsEndpoint {

    private static final String NAMESPACE_URI = "http://www.soap.dto/client";

    private ClientService clientService;

    private UserService userService;

    private ClientDetailsMapper clientDetailsMapper;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createOrEditClientRequest")
    @ResponsePayload
    public CreateOrEditClientResponse createOrEdit(@RequestPayload CreateOrEditClientRequest request) throws NoSuchAlgorithmException {
        UserInfoDTO user = userService.getByEmail(request.getEmail());
        ClientDetails clientDetails = request.getClientDetails();
        if (user == null && clientDetails.getPassword() == null) {
            return null;
        }

        Client client = clientDetailsMapper.toEntity(clientDetails);
        if (clientDetails.getId() != null) {
            clientDetails = clientDetailsMapper.toDto(clientService.edit(client.getId(), client));
        } else {
            clientDetails = clientDetailsMapper.toDto(clientService.create(client, true));
        }

        CreateOrEditClientResponse response = new CreateOrEditClientResponse();
        response.setClientDetails(clientDetails);
        return response;
    }

    @Autowired
    public ClientDetailsEndpoint(ClientService clientService, UserService userService, ClientDetailsMapper clientDetailsMapper) {
        this.clientService = clientService;
        this.userService = userService;
        this.clientDetailsMapper = clientDetailsMapper;
    }
}
