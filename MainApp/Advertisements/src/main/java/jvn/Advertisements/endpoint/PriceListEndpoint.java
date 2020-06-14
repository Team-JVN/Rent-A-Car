package jvn.Advertisements.endpoint;

import jvn.Advertisements.dto.soap.localhost._8080.advertisements.GetPriceListDetailsRequest;
import jvn.Advertisements.dto.soap.localhost._8080.advertisements.GetPriceListDetailsResponse;
import jvn.Advertisements.mapper.PriceListDetailsMapper;
import jvn.Advertisements.model.PriceList;
import jvn.Advertisements.service.PriceListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class PriceListEndpoint {

    private static final String NAMESPACE_URI = "/advertisements/ws";

    private PriceListService priceListService;

    private PriceListDetailsMapper priceListDetailsMapper;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getPriceListDetailsRequest")
    @ResponsePayload
    public GetPriceListDetailsResponse getPriceListDetails(@RequestPayload GetPriceListDetailsRequest request) {
        PriceList priceList = priceListDetailsMapper.toEntity(request.getPriceListDetails());
        PriceList createdPriceList = priceListService.create(priceList, 1L);

        GetPriceListDetailsResponse response = new GetPriceListDetailsResponse();
        response.setPriceListDetails(priceListDetailsMapper.toDto(createdPriceList));

        return response;
    }

    @Autowired
    public PriceListEndpoint(PriceListService priceListService, PriceListDetailsMapper priceListDetailsMapper) {
        this.priceListDetailsMapper = priceListDetailsMapper;
        this.priceListService = priceListService;
    }
}
