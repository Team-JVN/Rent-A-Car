package jvn.RentACar.client;

import jvn.RentACar.dto.soap.client.CreateOrEditClientRequest;
import jvn.RentACar.dto.soap.client.CreateOrEditClientResponse;
import jvn.RentACar.mapper.ClientDetailsMapper;
import jvn.RentACar.model.Client;
import jvn.RentACar.model.User;
import jvn.RentACar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

public class ClientClient extends WebServiceGatewaySupport {

    @Autowired
    private UserService userService;

    @Autowired
    private ClientDetailsMapper clientDetailsMapper;

    public CreateOrEditClientResponse createOrEdit(Client client) {

        CreateOrEditClientRequest request = new CreateOrEditClientRequest();
        request.setClientDetails(clientDetailsMapper.toDto(client));
        User user = userService.getLoginUser();
        if (user == null) {
            request.setEmail(client.getEmail());
        } else {
            request.setEmail(user.getEmail());
        }

        CreateOrEditClientResponse response = (CreateOrEditClientResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

}