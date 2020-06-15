package jvn.Advertisements.endpoint;

import jvn.Advertisements.dto.soap.pricelist.GetPriceListDetailsRequest;
import jvn.Advertisements.dto.soap.pricelist.GetPriceListDetailsResponse;
import jvn.Advertisements.dto.soap.pricelist.PriceListDetails;
import jvn.Advertisements.mapper.PriceListDetailsMapper;
import jvn.Advertisements.service.PriceListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class PriceListEndpoint {
    private static final String NAMESPACE_URI = "http://www.soap.dto/pricelist";

    private PriceListService priceListService;

    private PriceListDetailsMapper priceListDetailsMapper;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getPriceListDetailsRequest")
    @ResponsePayload
    public GetPriceListDetailsResponse createOrEdit(@RequestPayload GetPriceListDetailsRequest request) {
        PriceListDetails priceListDetails = request.getPriceListDetails();
        if (priceListDetails.getId() != null) {
            priceListDetails = priceListDetailsMapper.toDto(priceListService.edit(priceListDetails.getId(), priceListDetailsMapper.toEntity(priceListDetails), 2L));
        } else {

        }
        return response;
    }

    @Autowired
    public PriceListEndpoint(PriceListService priceListService, PriceListDetailsMapper priceListDetailsMapper) {
        this.priceListService = priceListService;
        this.priceListDetailsMapper;
    }
}
