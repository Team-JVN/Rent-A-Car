package jvn.Users.endpoint;

import jvn.Users.dto.response.UserInfoDTO;
import jvn.Users.dto.soap.client.*;
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
import java.util.List;
import java.util.stream.Collectors;

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

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "checkClientPersonalInfoRequest")
    @ResponsePayload
    public CheckClientPersonalInfoResponse checkClientPersonalInfo(@RequestPayload CheckClientPersonalInfoRequest request) throws NoSuchAlgorithmException {
        String valid = clientService.checkClientPersonalInfo(request.getClientEmail(), request.getPhoneNumber());

        CheckClientPersonalInfoResponse response = new CheckClientPersonalInfoResponse();
        response.setDataValid(valid);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteClientDetailsRequest")
    @ResponsePayload
    public DeleteClientDetailsResponse delete(@RequestPayload DeleteClientDetailsRequest request) {
        UserInfoDTO user = userService.getByEmail(request.getEmail());
        if (user == null) {
            return null;
        }
        DeleteClientDetailsResponse response = new DeleteClientDetailsResponse();
        response.setCanDelete(clientService.checkIfCanDeleteAndDelete(request.getId(), user.getId()));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllClientDetailsRequest")
    @ResponsePayload
    public GetAllClientDetailsResponse getAll(@RequestPayload GetAllClientDetailsRequest request) {
        UserInfoDTO user = userService.getByEmail(request.getEmail());
        if (user == null) {
            return null;
        }

        List<ClientDetails> list = clientService.getAll(user.getId()).stream().map(clientDetailsMapper::toDto).
                collect(Collectors.toList());
        GetAllClientDetailsResponse response = new GetAllClientDetailsResponse();
        response.getClientDetails().addAll(list);
        return response;
    }

    @Autowired
    public ClientDetailsEndpoint(ClientService clientService, UserService userService, ClientDetailsMapper clientDetailsMapper) {
        this.clientService = clientService;
        this.userService = userService;
        this.clientDetailsMapper = clientDetailsMapper;
    }
}
