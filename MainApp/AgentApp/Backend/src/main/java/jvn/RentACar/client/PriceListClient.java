package jvn.RentACar.client;

import jvn.RentACar.dto.soap.pricelist.*;
import jvn.RentACar.model.PriceList;
import jvn.RentACar.model.User;
import jvn.RentACar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

public class PriceListClient extends WebServiceGatewaySupport {

    @Autowired
    private UserService userService;

    public GetPriceListDetailsResponse createOrEdit(PriceList priceList) {
        PriceListDetails priceListDetails = new PriceListDetails();
        priceListDetails.setPricePerKm(priceList.getPricePerKm());
        priceListDetails.setPricePerCDW(priceList.getPriceForCDW());
        priceListDetails.setPricePerDay(priceList.getPricePerDay());
        priceListDetails.setId(priceList.getMainAppId());
        GetPriceListDetailsRequest request = new GetPriceListDetailsRequest();
        request.setPriceListDetails(priceListDetails);
        User user = userService.getLoginUser();
        if(user == null){
            return null;
        }
        request.setEmail(user.getEmail());
        GetPriceListDetailsResponse response = (GetPriceListDetailsResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }


    public DeletePriceListDetailsResponse checkAndDeleteIfCan(PriceList priceList) {
        DeletePriceListDetailsRequest request = new DeletePriceListDetailsRequest();
        request.setId(priceList.getMainAppId());
        User user = userService.getLoginUser();
        if(user == null){
            return null;
        }
        request.setEmail(user.getEmail());
        DeletePriceListDetailsResponse response = (DeletePriceListDetailsResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }
}