package jvn.Advertisements.endpoint;

import jvn.Advertisements.dto.soap.pricelist.GetPriceListDetailsRequest;
import jvn.Advertisements.dto.soap.pricelist.GetPriceListDetailsResponse;
import jvn.Advertisements.dto.soap.pricelist.PriceListDetails;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class PriceListEndpoint {
    private static final String NAMESPACE_URI = "http://www.soap.dto/pricelist";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getPriceListDetailsRequest")
    @ResponsePayload
    public GetPriceListDetailsResponse getCountry(@RequestPayload GetPriceListDetailsRequest request) {
        GetPriceListDetailsResponse response = new GetPriceListDetailsResponse();
        PriceListDetails details = new PriceListDetails();
        details.setId(1L);
        details.setPricePerCDW(1.0);
        details.setPricePerDay(25.0);
        details.setPricePerKm(35.0);
        response.setPriceListDetails(details);
        System.out.println("Haj");
        return response;
    }
}
