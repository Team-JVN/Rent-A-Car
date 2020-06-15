package jvn.Advertisements.endpoint;

import jvn.Advertisements.client.UserClient;
import jvn.Advertisements.dto.response.UserInfoDTO;
import jvn.Advertisements.dto.soap.pricelist.*;
import jvn.Advertisements.mapper.PriceListDetailsMapper;
import jvn.Advertisements.service.PriceListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;
import java.util.stream.Collectors;

@Endpoint
public class PriceListEndpoint {
    private static final String NAMESPACE_URI = "http://www.soap.dto/pricelist";

    private PriceListService priceListService;

    private PriceListDetailsMapper priceListDetailsMapper;

    private UserClient userClient;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getPriceListDetailsRequest")
    @ResponsePayload
    public GetPriceListDetailsResponse createOrEdit(@RequestPayload GetPriceListDetailsRequest request) {
        UserInfoDTO dto = userClient.getUser(request.getEmail());
        if (dto == null) {
            return null;
        }

        PriceListDetails priceListDetails = request.getPriceListDetails();
        if (priceListDetails.getId() != null) {
            priceListDetails = priceListDetailsMapper.toDto(priceListService.edit(priceListDetails.getId(), priceListDetailsMapper.toEntity(priceListDetails), dto.getId()));
        } else {
            priceListDetails = priceListDetailsMapper.toDto(priceListService.create(priceListDetailsMapper.toEntity(priceListDetails), dto.getId()));
        }
        GetPriceListDetailsResponse response = new GetPriceListDetailsResponse();
        response.setPriceListDetails(priceListDetails);
        return response;
    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deletePriceListDetailsRequest")
    @ResponsePayload
    public DeletePriceListDetailsResponse deletePriceList(@RequestPayload DeletePriceListDetailsRequest request) {
        UserInfoDTO dto = userClient.getUser(request.getEmail());
        if (dto == null) {
            return null;
        }
        DeletePriceListDetailsResponse response = new DeletePriceListDetailsResponse();
        response.setCanDelete(priceListService.checkIfCanDeleteAndDelete(request.getId(), dto.getId()));
        return response;
    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllPriceListDetailsRequest")
    @ResponsePayload
    public GetAllPriceListDetailsResponse createOrEdit(@RequestPayload GetAllPriceListDetailsRequest request) {
        UserInfoDTO dto = userClient.getUser(request.getEmail());
        if (dto == null) {
            return null;
        }

        List<PriceListDetails> list = priceListService.getPriceListsDeletedAndExisting(dto.getId()).stream().map(priceListDetailsMapper::toDto).
                collect(Collectors.toList());
        GetAllPriceListDetailsResponse response = new GetAllPriceListDetailsResponse();
        response.getPriceListDetails().addAll(list);
        return response;
    }

    @Autowired
    public PriceListEndpoint(PriceListService priceListService, PriceListDetailsMapper priceListDetailsMapper, UserClient userClient) {
        this.priceListService = priceListService;
        this.userClient = userClient;
        this.priceListDetailsMapper = priceListDetailsMapper;
    }
}
