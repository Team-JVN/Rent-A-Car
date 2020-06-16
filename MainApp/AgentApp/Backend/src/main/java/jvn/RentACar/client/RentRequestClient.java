package jvn.RentACar.client;

import jvn.RentACar.dto.soap.rentrequest.CreateRentRequestRequest;
import jvn.RentACar.dto.soap.rentrequest.CreateRentRequestResponse;
import jvn.RentACar.dto.soap.rentrequest.RentRequestDetails;
import jvn.RentACar.mapper.RentRequestDetailsMapper;
import jvn.RentACar.model.RentRequest;
import jvn.RentACar.model.User;
import jvn.RentACar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

public class RentRequestClient extends WebServiceGatewaySupport {

    @Autowired
    private UserService userService;

    @Autowired
    private RentRequestDetailsMapper rentRequestDetailsMapper;

    public CreateRentRequestResponse createOrEdit(RentRequest rentRequest) {
        RentRequestDetails rentRequestDetails = rentRequestDetailsMapper.toDto(rentRequest);

        CreateRentRequestRequest request = new CreateRentRequestRequest();
        request.setRentRequestDetails(rentRequestDetails);
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());
        CreateRentRequestResponse response = (CreateRentRequestResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

/*
    public DeletePriceListDetailsResponse checkAndDeleteIfCan(PriceList priceList) {
        DeletePriceListDetailsRequest request = new DeletePriceListDetailsRequest();
        request.setId(priceList.getMainAppId());
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());
        DeletePriceListDetailsResponse response = (DeletePriceListDetailsResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

    public GetAllPriceListDetailsResponse getAll() {
        GetAllPriceListDetailsRequest request = new GetAllPriceListDetailsRequest();
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());

        GetAllPriceListDetailsResponse response = (GetAllPriceListDetailsResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }
 */
}