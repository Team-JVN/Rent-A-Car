package jvn.RentACar.client;

import jvn.RentACar.dto.soap.client.*;
import jvn.RentACar.mapper.ClientDetailsMapper;
import jvn.RentACar.model.Client;
import jvn.RentACar.model.User;
import jvn.RentACar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


public class ClientClient extends WebServiceGatewaySupport {

    @Autowired
    private UserService userService;

    @Autowired
    private ClientDetailsMapper clientDetailsMapper;

//    @Autowired
//    @Qualifier("webServiceTemplateClient")
//    private WebServiceTemplate webServiceTem;

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

    public DeleteClientDetailsResponse checkAndDeleteIfCan(Client client) {
        DeleteClientDetailsRequest request = new DeleteClientDetailsRequest();
        request.setId(client.getMainAppId());
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());
        DeleteClientDetailsResponse response = (DeleteClientDetailsResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

    public CheckClientPersonalInfoResponse checkClientPersonalInfo(String clientEmail, String phoneNumber) {
        CheckClientPersonalInfoRequest request = new CheckClientPersonalInfoRequest();
        request.setClientEmail(clientEmail);
        request.setPhoneNumber(phoneNumber);
        CheckClientPersonalInfoResponse response = (CheckClientPersonalInfoResponse)getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

    public GetAllClientDetailsResponse getAll() {
        GetAllClientDetailsRequest request = new GetAllClientDetailsRequest();
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());

        GetAllClientDetailsResponse response = (GetAllClientDetailsResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

}