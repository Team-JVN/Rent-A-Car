package jvn.RentACar.client;

import jvn.RentACar.dto.soap.priceList.GetPriceListDetailsRequest;
import jvn.RentACar.dto.soap.priceList.GetPriceListDetailsResponse;
import jvn.RentACar.dto.soap.priceList.PriceListDetails;
import jvn.RentACar.model.PriceList;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

public class PriceListClient extends WebServiceGatewaySupport {

    public GetPriceListDetailsResponse create(PriceList priceList) {
        PriceListDetails priceListDetails = new PriceListDetails();
        priceListDetails.setPricePerKm(priceList.getPricePerKm());
        priceListDetails.setPricePerCDW(priceList.getPriceForCDW());
        priceListDetails.setPricePerDay(priceList.getPricePerDay());
        GetPriceListDetailsRequest request = new GetPriceListDetailsRequest();
        request.setPriceListDetails(priceListDetails);
        GetPriceListDetailsResponse response = (GetPriceListDetailsResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

}