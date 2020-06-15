package jvn.RentACar.client;

import jvn.RentACar.dto.soap.pricelist.GetPriceListDetailsRequest;
import jvn.RentACar.dto.soap.pricelist.GetPriceListDetailsResponse;
import jvn.RentACar.dto.soap.pricelist.PriceListDetails;
import jvn.RentACar.model.PriceList;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

public class PriceListClient extends WebServiceGatewaySupport {

    public GetPriceListDetailsResponse createOrEdit(PriceList priceList) {
        PriceListDetails priceListDetails = new PriceListDetails();
        priceListDetails.setPricePerKm(priceList.getPricePerKm());
        priceListDetails.setPricePerCDW(priceList.getPriceForCDW());
        priceListDetails.setPricePerDay(priceList.getPricePerDay());
        priceListDetails.setId(priceList.getMainAppId());
        GetPriceListDetailsRequest request = new GetPriceListDetailsRequest();
        request.setPriceListDetails(priceListDetails);
        GetPriceListDetailsResponse response = (GetPriceListDetailsResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

}